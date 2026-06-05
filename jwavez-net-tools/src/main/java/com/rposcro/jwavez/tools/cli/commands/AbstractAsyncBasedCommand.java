package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.NetworkTransportRequestBuilder;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.interceptors.CallbackInterceptor;
import com.rposcro.jwavez.serial.interceptors.FrameBufferInterceptor;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceBasedOptions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractAsyncBasedCommand extends AbstractCommand {

    protected GeneralAsynchronousController controller;
    protected NetworkTransportRequestBuilder transportRequestBuilder;

    private InterceptableCallbackHandler callbackHandler;

    private CompletableFuture<Object> futureCommand;
    private CommandType expectedCommandType;
    private SerialCommand expectedCallbackCommand;

    protected AbstractAsyncBasedCommand connect(AbstractDeviceBasedOptions options) throws SerialException {
        this.controller = GeneralAsynchronousController.builder()
                .dongleDevice(options.getDevice())
                .callbackHandler(this.callbackHandler = new InterceptableCallbackHandler())
                .build()
                .connect();
        this.transportRequestBuilder = JwzSerialSupport.defaultSupport().serialRequestFactory().networkTransportRequestBuilder();
        addCallbackInterceptor(this::handleSerialCommand);
        addCallbackInterceptor(new ApplicationCommandInterceptor()
                .registerCommandsListener(this::handleApplicationCommand));
        return this;
    }

    public AbstractAsyncBasedCommand addCallbackInterceptor(CallbackInterceptor interceptor) {
        callbackHandler.addCallbackInterceptor(interceptor);
        return this;
    }

    public AbstractAsyncBasedCommand addCallbackInterceptor(FrameBufferInterceptor interceptor) {
        callbackHandler.addFrameBufferInterceptor(interceptor);
        return this;
    }

    @Override
    public void close() throws SerialPortException {
        controller.close();
    }

    protected ZWaveCallback requestZWCallback(SerialRequest request, long timeout)
            throws SerialException {
        return requestZWCallback(request, request.getSerialCommand(), timeout);
    }

    protected <T extends ZWaveCallback> T requestZWCallback(SerialRequest request, SerialCommand expectedCallbackCommand, long timeout)
            throws SerialException {
        try {
            setFlowMode(expectedCallbackCommand);
            futureCommand = new CompletableFuture<>();
            controller.requestResponseFlow(request);
            return (T) futureCommand.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new FlowException("Request failed due to timeout");
        } catch (Exception e) {
            throw new FlowException("Unexpected exception occurred");
        } finally {
            resetFlow();
        }
    }

    protected <T extends ZWaveResponse> T requestZWResponse(SerialRequest request, long timeout)
            throws SerialException {
        try {
            resetFlow();
            return controller.requestResponseFlow(request);
        } catch (Exception e) {
            throw new FlowException("Unexpected exception occurred");
        }
    }

    protected void processSendDataRequest(NodeId hostNodeId, ZWaveControlledCommand command) throws SerialException {
        resetFlow();
        SendDataCallback callback = controller.requestCallbackFlow(transportRequestBuilder.createSendDataRequest(hostNodeId, command, nextFlowId()));
        if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
            throw new FlowException("Dongle failed to deliver request: " + callback.getTransmitCompletionStatus());
        }
    }

    protected <T extends ZWaveSupportedCommand> T requestApplicationCommand(
            NodeId nodeId, ZWaveControlledCommand applicationCommand, CommandType expectedReturnedCommandType, long timeout)
            throws SerialException {

        SerialRequest request = transportRequestBuilder.createSendDataRequest(nodeId, applicationCommand, nextFlowId());
        return requestApplicationCommand(request, expectedReturnedCommandType, timeout);
    }

    protected <T extends ZWaveSupportedCommand> T requestApplicationCommand(SerialRequest request, CommandType expectedCommandType, long timeout)
            throws SerialException {
        try {
            setFlowMode(expectedCommandType);
            futureCommand = new CompletableFuture<>();

            SendDataCallback callback = controller.requestCallbackFlow(request);
            if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
                throw new FlowException("Dongle failed to deliver data: " + callback.getTransmitCompletionStatus());
            }

            try {
                return (T) futureCommand.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new FlowException("Request failed due to timeout");
            } catch (Exception e) {
                throw new FlowException("Unexpected exception occurred");
            }
        } finally {
            resetFlow();
        }
    }

    private void handleSerialCommand(ZWaveCallback callback) {
        if (callback.getSerialCommand() == expectedCallbackCommand) {
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
        this.expectedCallbackCommand = null;
    }

    private void setFlowMode(SerialCommand serialCommand) {
        this.expectedCommandType = null;
        this.expectedCallbackCommand = serialCommand;
    }

    private void resetFlow() {
        this.expectedCommandType = null;
        this.expectedCallbackCommand = null;
    }
}
