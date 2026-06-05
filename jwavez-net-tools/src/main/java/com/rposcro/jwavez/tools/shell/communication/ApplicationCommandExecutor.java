package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.listeners.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.requests.NetworkTransportRequestBuilder;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
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
    private NetworkTransportRequestBuilder transportRequestBuilder;

    // Session based members
    private CompletableFuture<ApplicationCommandResult> futureCommand;
    private ApplicationCommandResult.ApplicationCommandResultBuilder resultBuilder = ApplicationCommandResult.builder();
    private CommandType expectedCommandType;
    private String sessionId;

    @Builder
    public ApplicationCommandExecutor(@NonNull String device, Long timeoutMillis, NetworkTransportRequestBuilder transportRequestBuilder)
            throws SerialPortException {
        this.transportRequestBuilder = transportRequestBuilder;

        ApplicationCommandInterceptor appCmdInterceptor = ApplicationCommandInterceptor.builder()
                .skipUnsupportedCallbacks(true)
                .supportBroadcasts(false)
                .supportMulticasts(false)
                .supportedCommandDispatcher(new SupportedCommandDispatcher())
                .build();
        appCmdInterceptor.registerCommandsListener(this::handleApplicationCommand);

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        callbackHandler.addFrameBufferInterceptor(this::interceptSerialCallbackBuffer);
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
        SerialRequest request = transportRequestBuilder.createSendDataRequest(nodeId, applicationCommand, SerialUtils.nextFlowId());
        return requestApplicationCommand(request, null, timeout);
    }

    public <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> requestApplicationCommand(
            NodeId nodeId,
            ZWaveControlledCommand applicationCommand,
            CommandType expectedReturnedCommandType,
            long timeout) throws SerialException {
        SerialRequest request = transportRequestBuilder.createSendDataRequest(nodeId, applicationCommand, SerialUtils.nextFlowId());
        return requestApplicationCommand(request, expectedReturnedCommandType, timeout);
    }

    private <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> requestApplicationCommand(
            SerialRequest request,
            CommandType expectedCommandType,
            long timeout)
            throws SerialException {
        try {
            initExecutionSession(expectedCommandType);
            log.debug("{}: Requesting application command {} expecting response type {}", sessionId, request.getSerialCommand(), expectedCommandType);

            SendDataCallback callback = controller.requestCallbackFlow(request);
            log.debug("{}: Received callback {}", sessionId, callback.asFineString());
            if (callback.getTransmitCompletionStatus() != TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
                throw new FlowException("Dongle failed to deliver data, transmit status received is: " + callback.getTransmitCompletionStatus());
            }

            try {
                return futureCommand.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new FlowException("Request failed due to timeout");
            } catch (Exception e) {
                throw new FlowException("Unexpected exception occurred");
            }
        } finally {
            cancelExecutionSession();
        }
    }

    private void handleSerialResponse(ImmutableBuffer responseBuffer) {
        if (isSessionActive()) {
            log.debug("{}: Received response buffer", sessionId);
            resultBuilder.serialResponsePayload(responseBuffer.cloneBytes());
        } else {
            log.debug("Received response outside of any session");
        }
    }

    private void interceptSerialCallback(ZWaveCallback callback) {
        if (isSessionActive()) {
            log.debug("{}: Received callback {}", sessionId, callback.asFineString());
            resultBuilder.serialCallback(callback);
        } else {
            log.debug("Received callback outside of any session {}", callback.asFineString());
        }
    }

    private void interceptSerialCallbackBuffer(ImmutableBuffer callbackBuffer) {
        if (isSessionActive()) {
            log.debug("{}: Received callback buffer", sessionId);
            resultBuilder.serialCallbackPayload(callbackBuffer.cloneBytes());
        } else {
            log.debug("Received callback outside of any session");
        }
    }

    private void handleApplicationCommand(ZWaveSupportedCommand command) {
        if (isSessionActive()) {
            log.debug("{}: Received supported command {}", sessionId, command.asNiceString());
            if (expectedCommandType == null || expectedCommandType == command.getCommandType()) {
                resultBuilder.acquiredSupportedCommand(command);
                futureCommand.complete(resultBuilder.build());
            } else {
                log.info("Skipped application command: %s", command.asNiceString());
            }
        } else {
            log.debug("Received supported command outside of any session {}", command.asNiceString());
        }
    }

    private void initExecutionSession(CommandType commandType) {
        this.futureCommand = new CompletableFuture<>();
        this.resultBuilder = ApplicationCommandResult.builder();
        this.expectedCommandType = commandType;
        this.sessionId = System.currentTimeMillis() + "";
        log.debug("{}: Session initialized", sessionId);
    }

    private void cancelExecutionSession() {
        log.debug("{}: Session closed", sessionId);
        this.futureCommand = null;
        this.resultBuilder = null;
        this.expectedCommandType = null;
        this.sessionId = null;
    }

    private boolean isSessionActive() {
        return this.sessionId != null;
    }
}
