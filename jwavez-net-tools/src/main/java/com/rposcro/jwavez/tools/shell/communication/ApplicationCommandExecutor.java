package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ApplicationCommandExecutor {

    private GeneralAsynchronousController controller;
    private CompletableFuture<ApplicationCommandResult> futureCommand;

    private CommandType expectedCommandType;
    private ApplicationCommandResult.ApplicationCommandResultBuilder resultBuilder = ApplicationCommandResult.builder();

    @Builder
    public ApplicationCommandExecutor(@NonNull String device, Long timeoutMillis) throws SerialPortException {
        ApplicationCommandInterceptor appCmdInterceptor = ApplicationCommandInterceptor.builder()
                .skipUnsupportedCallbacks(true)
                .supportBroadcasts(false)
                .supportMulticasts(false)
                .supportedCommandDispatcher(new SupportedCommandDispatcher())
                .build();
        appCmdInterceptor.registerAllCommandsHandler(this::handleApplicationCommand);

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        callbackHandler.addViewBufferInterceptor(this::interceptSerialCallbackBuffer);
        callbackHandler.addCallbackInterceptor(this::interceptSerialCallback);
        callbackHandler.addCallbackInterceptor(appCmdInterceptor);

        this.controller = GeneralAsynchronousController.builder()
                .dongleDevice(device)
                .callbackHandler(callbackHandler)
                .responseHandler(this::handleSerialResponse)
                .timeoutMillis(timeoutMillis != null ? timeoutMillis : SerialUtils.DEFAULT_TIMEOUT)
                .rxTxConfiguration(RxTxConfiguration.builder()
                        .requestRetriesMaxCount(0)
                        .build())
                .build()
                .connect();
    }

    public void close() throws SerialPortException {
        this.controller.close();
    }

    public <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> requestApplicationCommand(
            NodeId nodeId,
            ZWaveControlledCommand applicationCommand,
            long timeout) throws SerialException {
        SerialRequest request = SendDataRequest.createSendDataRequest(nodeId, applicationCommand, SerialUtils.nextFlowId());
        return requestApplicationCommand(request, null, timeout);
    }

    public <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> requestApplicationCommand(
            NodeId nodeId,
            ZWaveControlledCommand applicationCommand,
            CommandType expectedReturnedCommandType,
            long timeout) throws SerialException {
        SerialRequest request = SendDataRequest.createSendDataRequest(nodeId, applicationCommand, SerialUtils.nextFlowId());
        return requestApplicationCommand(request, expectedReturnedCommandType, timeout);
    }

    private <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> requestApplicationCommand(
            SerialRequest request,
            CommandType expectedCommandType,
            long timeout)
            throws SerialException {
        try {
            initExecutionContext(expectedCommandType);
            this.futureCommand = new CompletableFuture<>();

            SendDataCallback callback = controller.requestCallbackFlow(request);
            if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
                throw new FlowException("Dongle failed to deliver data: " + callback.getTransmitCompletionStatus());
            }

            try {
                return futureCommand.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new FlowException("Request failed due to timeout");
            } catch (Exception e) {
                throw new FlowException("Unexpected exception occurred");
            }
        } finally {
            cancelExecutionContext();
        }
    }

    private void handleSerialResponse(ViewBuffer responseBuffer) {
        resultBuilder.serialResponsePayload(responseBuffer.copyBytes());
    }

    private void interceptSerialCallback(ZWaveCallback callback) {
        resultBuilder.serialCallback(callback);
    }

    private void interceptSerialCallbackBuffer(ViewBuffer callbackBuffer) {
        resultBuilder.serialCallbackPayload(callbackBuffer.copyBytes());
    }

    private void handleApplicationCommand(ZWaveSupportedCommand command) {
        if (expectedCommandType == null || expectedCommandType == command.getCommandType()) {
            resultBuilder.acquiredSupportedCommand(command);
            futureCommand.complete(resultBuilder.build());
        } else {
            log.info("Skipped application command: %s", command.asNiceString());
        }
    }

    private void initExecutionContext(CommandType commandType) {
        this.expectedCommandType = commandType;
        this.resultBuilder = ApplicationCommandResult.builder();
    }

    private void cancelExecutionContext() {
        this.expectedCommandType = null;
        this.resultBuilder = null;
    }
}
