package com.rposcro.jwavez.serial.transactions;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.callbacks.SetSUCNodeIdCallbackFrame;
import com.rposcro.jwavez.serial.frame.requests.SetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.SetSUCNodeIdResponseFrame;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetSUCNodeIdTransaction extends AbstractSerialTransaction<Void> {

  private static final long TIMEOUT_PROTOCOL_READY = 10 * 1000;
  private static final long TIMEOUT_NODE_FOUND = 60 * 1000;
  private final Map<Phase, Consumer<SOFFrame>> phaseHandlers;

  private NodeId nodeId;
  private boolean localController;
  private boolean enableSucAndSis;

  private boolean deliveryConfirmed;
  private byte callbackId;
  private Phase phase;

  {
    phaseHandlers = new HashMap<>();
    phaseHandlers.put(Phase.REQUEST_SEND, this::receivedAtRequestSend);
    phaseHandlers.put(Phase.PROCESS_STARTED, this::receivedAtProcessStarted);
  }

  public SetSUCNodeIdTransaction(NodeId nodeId, boolean enableSucAndSis, boolean localController) {
    super(true, true);
    this.nodeId = nodeId;
    this.localController = localController;
    this.enableSucAndSis = enableSucAndSis;
  }

  @Override
  public TransactionContext<Void> init(TransactionId transactionId) {
    super.init(transactionId);
    callbackId = transactionContext.getTransactionId().getCallbackId();
    return transactionContext;
  }

  @Override
  public SetSUCNodeIdRequestFrame startUp() {
    setPhase(Phase.REQUEST_SEND);
    deliveryConfirmed = false;
    return startUpFrame();
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame inboundFrame) {
    try {
      validateFrameReadiness();
      Consumer<SOFFrame> function = phaseHandlers.get(phase);

      if (function != null) {
        function.accept(inboundFrame);
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

    return Optional.empty();
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

  private void receivedAtRequestSend(SOFFrame inboundFrame) {
    SetSUCNodeIdResponseFrame responseFrame = validateResponseAndCast(inboundFrame);
    if (responseFrame.isRequestAccepted()) {
      if (localController) {
        setPhase(Phase.END);
        completeTransaction(null);
      } else {
        setPhase(Phase.PROCESS_STARTED);
      }
    } else {
      setPhase(Phase.END);
      failTransaction();
    }
  }

  private void receivedAtProcessStarted(SOFFrame inboundFrame) {
    SetSUCNodeIdCallbackFrame callbackFrame = validateCallbackAndCast(inboundFrame, callbackId);
    setPhase(Phase.END);
    if (callbackFrame.isSuccessful()) {
      completeTransaction(null);
    } else {
      failTransaction();
    }
  }

  private SetSUCNodeIdRequestFrame startUpFrame() {
    return new SetSUCNodeIdRequestFrame(nodeId, enableSucAndSis, callbackId);
  }

  private void validateFrameReadiness() throws TransactionException {
    if (!deliveryConfirmed) {
      throw new TransactionException("Received frame but prior hasn't been confirmed yet, stopping");
    }
    if (phase == Phase.IDLE || phase == Phase.END) {
      throw new TransactionException("Frames are not expected at phase " + phase);
    }
  }

  private void setPhase(Phase phase) {
    this.phase = phase;
    log.debug("Phase changed to " + phase);
  }

  private enum Phase {
    IDLE,
    REQUEST_SEND,
    PROCESS_STARTED,
    END,
  }
}
