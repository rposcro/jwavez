package com.rposcro.jwavez.serial.transactions;

import static com.rposcro.jwavez.serial.frame.SOFFrame.*;
import static com.rposcro.jwavez.serial.frame.constants.AddNodeToNeworkStatus.*;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.exceptions.TransactionException;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.SOFFrame;
import com.rposcro.jwavez.serial.frame.callbacks.AddNodeToNetworkCallbackFrame;
import com.rposcro.jwavez.serial.frame.constants.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.frame.constants.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.frame.requests.AddNodeToNetworkRequestFrame;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddNodeToNetworkTransaction extends AbstractSerialTransaction<NodeInfo> {

  private static final long TIMEOUT_PROTOCOL_READY = 10 * 1000;
  private static final long TIMEOUT_NODE_FOUND = 60 * 1000;
  private final Map<Phase, java.util.function.Function<AddNodeToNetworkCallbackFrame, Optional<SOFFrame>>> phaseHandlers;

  private boolean deliveryConfirmed;
  private byte callbackId;
  private Phase phase;
  private Optional<NodeInfo> nodeInfo;

  {
    phaseHandlers = new HashMap<>();
    phaseHandlers.put(Phase.WAITING_FOR_PROTOCOL, this::receivedAtWaitingForProtocol);
    phaseHandlers.put(Phase.ABORTING_OPERATION, this::receivedAtAbortingOperation);
    phaseHandlers.put(Phase.WAITING_FOR_NODE, this::receivedAtWaitingForNode);
    phaseHandlers.put(Phase.NODE_FOUND, this::receivedAtNodeFound);
    phaseHandlers.put(Phase.SLAVE_FOUND, this::receivedAtSlaveFound);
    phaseHandlers.put(Phase.CONTROLLER_FOUND, this::receivedAtControllerFound);
    phaseHandlers.put(Phase.CLEANING_UP_ERRORS, this::receivedAtCleaningErrors);
    phaseHandlers.put(Phase.TERMINATING_ADD_NODE, this::receivedAtTerminatingAddNodeOperation);
  }

  public AddNodeToNetworkTransaction() {
    super(true, false);
  }

  @Override
  public Future<TransactionResult<NodeInfo>> init(TransactionContext transactionContext) {
    setPhase(Phase.IDLE);
    callbackId = transactionContext.getTransactionId().getCallbackId();
    return super.init(transactionContext);
  }

  @Override
  public AddNodeToNetworkRequestFrame startUp() {
    setPhase(Phase.WAITING_FOR_PROTOCOL);
    deliveryConfirmed = false;
    return new AddNodeToNetworkRequestFrame(AddNodeToNeworkMode.ADD_NODE_ANY, callbackId, true, true);
  }

  @Override
  public Optional<SOFFrame> acceptInboundFrame(SOFFrame frame) {
    Optional<SOFFrame> nextFrameOption;

    try {
      validateCallbackReadiness();
      validateCallback(frame);
      AddNodeToNetworkCallbackFrame callbackFrame = (AddNodeToNetworkCallbackFrame) frame;
      java.util.function.Function<AddNodeToNetworkCallbackFrame, Optional<SOFFrame>> function = phaseHandlers.get(phase);

      if (function != null) {
        nextFrameOption = function.apply(callbackFrame);
      } else {
        log.error("Callback frame is not handled in phase {}", phase);
        setPhase(Phase.END);
        failTransaction();
        nextFrameOption = finalStopFrame();
      }
    } catch(TransactionException e) {
      log.error(e.getMessage());
      setPhase(Phase.END);
      failTransaction();
      nextFrameOption = finalStopFrame();
    }

    nextFrameOption.ifPresent((frm) -> { deliveryConfirmed = false; });
    return nextFrameOption;
  }

  @Override
  public void deliverySuccessful() {
    if (deliveryConfirmed) {
      log.warn("Received order delivery confirmation while it has already been confirmed");
    }
    switch(phase) {
      case FINAL_STOP_SENT:
        deliveryConfirmed = true;
        setPhase(Phase.END);
        completeTransaction(nodeInfo.orElse(null));
        break;
      default:
        deliveryConfirmed = true;
    }
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

  private Optional<SOFFrame> receivedAtWaitingForProtocol(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.CLEANING_UP_ERRORS);
      return stoppingFrame();
    } else if (status == ADD_NODE_STATUS_LEARN_READY) {
      setPhase(Phase.WAITING_FOR_NODE);
      return Optional.empty();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtWaitingForNode(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.CLEANING_UP_ERRORS);
      return stoppingFrame();
    } else if (status == ADD_NODE_STATUS_NODE_FOUND) {
      setPhase(Phase.NODE_FOUND);
      return Optional.empty();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtNodeFound(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.CLEANING_UP_ERRORS);
      return stoppingFrame();
    } else if (status == ADD_NODE_STATUS_ADDING_SLAVE) {
      nodeInfo = callbackFrame.getNodeInfo();
      setPhase(Phase.SLAVE_FOUND);
      return Optional.empty();
    } else if (status == ADD_NODE_STATUS_ADDING_CONTROLLER) {
      nodeInfo = callbackFrame.getNodeInfo();
      setPhase(Phase.CONTROLLER_FOUND);
      return Optional.empty();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtSlaveFound(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.CLEANING_UP_ERRORS);
      return stoppingFrame();
    } else if (status == ADD_NODE_STATUS_PROTOCOL_DONE) {
      setPhase(Phase.TERMINATING_ADD_NODE);
      return stoppingFrame();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtControllerFound(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.CLEANING_UP_ERRORS);
      return stoppingFrame();
    } else if (status == ADD_NODE_STATUS_PROTOCOL_DONE) {
      nodeInfo = callbackFrame.getNodeInfo();
      // TODO: Consider additional actions like SUC, SIS setup or controller replication
      setPhase(Phase.TERMINATING_ADD_NODE);
      return stoppingFrame();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtTerminatingAddNodeOperation(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.FINAL_STOP_SENT);
      return finalStopFrame();
    } else if (status == ADD_NODE_STATUS_DONE) {
      setPhase(Phase.FINAL_STOP_SENT);
      return finalStopFrame();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtAbortingOperation(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.FINAL_STOP_SENT);
      return finalStopFrame();
    } else if (status == ADD_NODE_STATUS_DONE) {
      setPhase(Phase.FINAL_STOP_SENT);
      return finalStopFrame();
    } else if (status == ADD_NODE_STATUS_NODE_FOUND) {
      setPhase(Phase.NODE_FOUND);
      return Optional.empty();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> receivedAtCleaningErrors(AddNodeToNetworkCallbackFrame callbackFrame) {
    AddNodeToNeworkStatus status = callbackFrame.getStatus();
    if (status == ADD_NODE_STATUS_FAILED) {
      setPhase(Phase.FINAL_STOP_SENT);
      return finalStopFrame();
    } else if (status == ADD_NODE_STATUS_DONE) {
      setPhase(Phase.FINAL_STOP_SENT);
      return finalStopFrame();
    } else {
      throw unexpectedStatusException(status);
    }
  }

  private Optional<SOFFrame> stoppingFrame() {
    return Optional.of(new AddNodeToNetworkRequestFrame(AddNodeToNeworkMode.ADD_NODE_STOP, callbackId));
  }

  private Optional<SOFFrame> finalStopFrame() {
    return Optional.of(new AddNodeToNetworkRequestFrame(AddNodeToNeworkMode.ADD_NODE_STOP, (byte) 0));
  }

  private void validateCallbackReadiness() throws TransactionException {
    if (!deliveryConfirmed) {
      throw new TransactionException("Received frame but prior hasn't been confirmed yet, stopping");
    }
    if (phase == Phase.IDLE || phase == Phase.END) {
      throw new TransactionException("Callback frames are not expected at phase " + phase);
    }
  }

  private void validateCallback(SOFFrame frame) throws TransactionException {
    if (frame instanceof AddNodeToNetworkCallbackFrame
      && frame.getFrameType() == FrameType.REQ
      && frame.getSerialCommand() == SerialCommand.ADD_NODE_TO_NETWORK
      && frame.getBuffer()[OFFSET_PAYLOAD] == callbackId) {
      return;
    }
    throw new TransactionException("Callback frame validation failed, stopping");
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
    WAITING_FOR_PROTOCOL,
    ABORTING_OPERATION,
    WAITING_FOR_NODE,
    NODE_FOUND,
    SLAVE_FOUND,
    CONTROLLER_FOUND,
    CLEANING_UP_ERRORS,
    TERMINATING_ADD_NODE,
    FINAL_STOP_SENT,
    END,
  }
}
