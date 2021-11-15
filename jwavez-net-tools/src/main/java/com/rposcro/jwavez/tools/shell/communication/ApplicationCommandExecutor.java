package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import lombok.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ApplicationCommandExecutor {

    private GeneralAsynchronousController controller;
    private CompletableFuture<Object> futureCommand;
    private CommandType expectedCommandType;
    private SerialCommand expectedSerialCommand;

    @Builder
    public ApplicationCommandExecutor(String device) throws SerialPortException {
        ApplicationCommandInterceptor appCmdInterceptor = ApplicationCommandInterceptor.builder()
                .skipUnsupportedCallbacks(true)
                .supportBroadcasts(false)
                .supportMulticasts(false)
                .supportedCommandDispatcher(new SupportedCommandDispatcher())
                .build();
        appCmdInterceptor.registerAllCommandsHandler(this::handleApplicationCommand);

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        callbackHandler.addCallbackInterceptor(this::handleSerialCommand);
        callbackHandler.addCallbackInterceptor(appCmdInterceptor);

        this.controller = GeneralAsynchronousController.builder()
                .dongleDevice(device)
                .callbackHandler(callbackHandler)
                .build()
                .connect();
    }

    public void close() throws SerialPortException {
        this.controller.close();
    }

    public <T extends ZWaveCallback> T requestZWCallback(SerialRequest request, SerialCommand expectedCallbackCommand, long timeout)
            throws SerialException {
        try {
            setFlowMode(expectedCallbackCommand);
            futureCommand = new CompletableFuture<>();
            controller.requestResponseFlow(request);
            return (T) futureCommand.get(timeout, TimeUnit.MILLISECONDS);
        } catch(TimeoutException e) {
            throw new FlowException("Request failed due to timeout");
        } catch(Exception e) {
            throw new FlowException("Unexpected exception occurred");
        } finally {
            resetFlow();
        }
    }

    public <T extends ZWaveSupportedCommand> T requestApplicationCommand(
            NodeId nodeId, ZWaveControlledCommand applicationCommand, CommandType expectedReturnedCommandType, long timeout)
            throws SerialException {

        SerialRequest request = SendDataRequest.createSendDataRequest(nodeId, applicationCommand, SerialUtils.nextFlowId());
        return requestApplicationCommand(request, expectedReturnedCommandType, timeout);
    }

    private <T extends ZWaveSupportedCommand> T requestApplicationCommand(SerialRequest request, CommandType expectedCommandType, long timeout)
            throws SerialException {
        try {
            setFlowMode(expectedCommandType);
            this.futureCommand = new CompletableFuture<>();

            SendDataCallback callback = controller.requestCallbackFlow(request);
            if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
                throw new FlowException("Dongle failed to deliver data: " + callback.getTransmitCompletionStatus());
            }

            try {
                return (T) futureCommand.get(timeout, TimeUnit.MILLISECONDS);
            } catch(TimeoutException e) {
                throw new FlowException("Request failed due to timeout");
            } catch(Exception e) {
                throw new FlowException("Unexpected exception occurred");
            }
        } finally {
            resetFlow();
        }
    }

    private void handleSerialCommand(ZWaveCallback callback) {
        if (callback.getSerialCommand() == expectedSerialCommand) {
            futureCommand.complete(callback);
        }
    }

    private void handleApplicationCommand(ZWaveSupportedCommand command) {
        if (command.getCommandType() == expectedCommandType) {
            futureCommand.complete(command);
        } else {
            System.out.printf("Skipped application command %s %s from %s\n",
                    command.getCommandClass(),
                    command.getCommandType(),
                    command.getSourceNodeId().getId());
        }
    }

    private void setFlowMode(CommandType commandType) {
        this.expectedCommandType = commandType;
        this.expectedSerialCommand = null;
    }

    private void setFlowMode(SerialCommand serialCommand) {
        this.expectedCommandType = null;
        this.expectedSerialCommand = serialCommand;
    }

    private void resetFlow() {
        this.expectedCommandType = null;
        this.expectedSerialCommand = null;
    }
}
