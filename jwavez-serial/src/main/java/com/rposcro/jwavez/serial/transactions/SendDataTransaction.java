package com.rposcro.jwavez.serial.transactions;

import static com.rposcro.jwavez.serial.frame.SOFFrame.OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.contants.FrameType;
import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.callbacks.SendDataCallbackFrame;
import com.rposcro.jwavez.serial.frame.contants.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.frame.contants.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.frame.requests.SendDataAbortRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.SendDataRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.SendDataResponseFrame;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendDataTransaction extends AbstractSerialTransaction<Void> {

  private static final long TIMEOUT_PROTOCOL_READY = 10 * 1000;
  private static final long TIMEOUT_NODE_FOUND = 60 * 1000;
  private final Map<Phase, java.util.function.Function<SOFFrame, Optional<SOFFrame>>> phaseHandlers;

  private NodeId nodeId;
  private ZWaveControlledCommand command;

  private boolean deliveryConfirmed;
  private byte callbackId;
  private Phase phase;

  {
    phaseHandlers = new HashMap<>();
    phaseHandlers.put(Phase.PREPARING_SENDING_DATA, this::receivedAtPreparingSendingData);
    phaseHandlers.put(Phase.SENDING_DATA, this::receivedAtSendingData);
  }

  public SendDataTransaction(NodeId nodeId, ZWaveControlledCommand command) {
    super(true, true);
    this.nodeId = nodeId;
    this.command = command;
  }

  @Override
  public TransactionContext<Void> init(TransactionId transactionId) {
    super.init(transactionId);
    callbackId = transactionContext.getTransactionId().getCallbackId();
    return transactionContext;
  }

  @Override
  public SOFRequestFrame startUp() {
    setPhase(Phase.PREPARING_SENDING_DATA);
    deliveryConfirmed = false;
    return startUpFrame();
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame inboundFrame) {
    Optional<SOFFrame> nextFrameOption = Optional.empty();

    try {
      validateFrameReadiness();
      java.util.function.Function<SOFFrame, Optional<SOFFrame>> function = phaseHandlers.get(phase);

      if (function != null) {
        nextFrameOption = function.apply(inboundFrame);
      } else {
        log.error("Inbound frame is not handled in phase {}", phase);
        setPhase(Phase.END);
        failTransaction();
      }
    } catch(TransactionException e) {
      log.error(e.getMessage());
      setPhase(Phase.END);
      failTransaction();
    }

    nextFrameOption.ifPresent((frm) -> { deliveryConfirmed = false; });
    return nextFrameOption;
  }

  @Override
  public void deliverySuccessful() {
    if (deliveryConfirmed) {
      log.warn("Received order delivery confirmation while it has already been confirmed");
    }
    deliveryConfirmed = true;
  }

  @Override
  public void deliveryFailed() {
    if (deliveryConfirmed) {
      log.warn("Received order delivery failure while it has already been confirmed");
    }
    setPhase(Phase.END);
    failTransaction();
  }

  @Override
  public void timeoutOccurred() {
    setPhase(Phase.END);
    failTransaction();
  }

  private Optional<SOFFrame> receivedAtPreparingSendingData(SOFFrame inboundFrame) {
    validateSendResponse(inboundFrame);
    SendDataResponseFrame responseFrame = (SendDataResponseFrame) inboundFrame;
    if (responseFrame.isSendingQueued()) {
      setPhase(Phase.SENDING_DATA);
    } else {
      setPhase(Phase.END);
      failTransaction();
    }
    return Optional.empty();
  }

  private Optional<SOFFrame> receivedAtSendingData(SOFFrame inboundFrame) {
    validateSendCallback(inboundFrame);
    SendDataCallbackFrame callbackFrame = (SendDataCallbackFrame) inboundFrame;
    TransmitCompletionStatus status = callbackFrame.getStatus();
    setPhase(Phase.END);
    if (status == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
      completeTransaction(null);
    } else if (status == TransmitCompletionStatus.TRANSMIT_COMPLETE_NO_ACK) {
      // special weak up treatment needed here, now just failing
      log.warn("Unsupported NO_ACK situation, needs to be implemented");
      failTransaction();
    } else {
      failTransaction();
    }
    return Optional.empty();
  }

  private SendDataRequestFrame startUpFrame() {
    return new SendDataRequestFrame(nodeId, callbackId, command);
  }

  private SendDataAbortRequestFrame abortFrame() {
    return new SendDataAbortRequestFrame();
  }

  private void validateFrameReadiness() throws TransactionException {
    if (!deliveryConfirmed) {
      throw new TransactionException("Received frame but prior hasn't been confirmed yet, stopping");
    }
    if (phase == Phase.IDLE || phase == Phase.END) {
      throw new TransactionException("Frames are not expected at phase " + phase);
    }
  }

  private void validateSendCallback(SOFFrame frame) throws TransactionException {
    if (frame instanceof SendDataCallbackFrame
      && frame.getFrameType() == FrameType.REQ
      && frame.getSerialCommand() == SerialCommand.SEND_DATA
      && frame.getBuffer()[OFFSET_PAYLOAD] == callbackId) {
      return;
    }
    throw new TransactionException("Callback frame validation failed, stopping");
  }

  private void validateSendResponse(SOFFrame frame) throws TransactionException {
    if (frame instanceof SendDataResponseFrame
      && frame.getFrameType() == FrameType.RES
      && frame.getSerialCommand() == SerialCommand.SEND_DATA) {
      return;
    }
    throw new TransactionException("Response frame validation failed, stopping");
  }

  private void setPhase(Phase phase) {
    this.phase = phase;
    log.debug("Phase changed to " + phase);
  }

  private TransactionException unexpectedStatusException(AddNodeToNeworkStatus receivedStatus) {
    return new TransactionException(String.format("Unsupported status received at %s phase: %s", phase, status));
  }

  private enum Phase {
    IDLE,
    PREPARING_SENDING_DATA,
    SENDING_DATA,
    PREPARING_RESENDING,
    END,
  }
}
