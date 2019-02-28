package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.enums.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.options.node.AbstractNodeBasedOptions;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractSendDataBasedCommand extends AbstractAsyncBasedCommand {

  private CompletableFuture<ZWaveSupportedCommand> futureCommand;
  private CommandType expectedCommandType;

  protected void connect(AbstractNodeBasedOptions options) throws CommandExecutionException {
    super.connect(options)
        .addCallbackInterceptor(new ApplicationCommandInterceptor()
            .registerAllCommandsHandler(this::handleZWaveCommand));
  }

  protected void processSendDataRequest(NodeId hostNodeId, ZWaveControlledCommand command) throws FlowException {
    SendDataCallback callback = controller.requestCallbackFlow(SendDataRequest.createSendDataRequest(hostNodeId, command, nextFlowId()));
    if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
      throw new FlowException("Dongle failed to deliver request: " + callback.getTransmitCompletionStatus());
    }
  }

  protected ZWaveSupportedCommand requestZWCommand(SerialRequest request, CommandType expectedCommandType, long timeout)
  throws FlowException {
    this.expectedCommandType = expectedCommandType;
    futureCommand = new CompletableFuture<>();

    SendDataCallback callback = controller.requestCallbackFlow(request);
    if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
      throw new FlowException("Dongle failed to deliver data: " + callback.getTransmitCompletionStatus());
    }

    try {
      return futureCommand.get(timeout, TimeUnit.MILLISECONDS);
    } catch(TimeoutException e) {
      throw new FlowException("Request failed due to timeout");
    } catch(Exception e) {
      throw new FlowException("Unexpected exception occurred");
    }
  }

  private void handleZWaveCommand(ZWaveSupportedCommand command) {
    if (command.getCommandType() == expectedCommandType) {
      futureCommand.complete(command);
    }
  }
}
