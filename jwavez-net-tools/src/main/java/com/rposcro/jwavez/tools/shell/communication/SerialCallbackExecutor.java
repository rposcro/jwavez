package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
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
public class SerialCallbackExecutor {

    private GeneralAsynchronousController controller;
    private CompletableFuture<Object> futureCommand;

    private SerialCommand expectedSerialCommand;

    @Builder
    public SerialCallbackExecutor(@NonNull String device, Long timeoutMillis) throws SerialPortException {
        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        callbackHandler.addCallbackInterceptor(this::interceptSerialCallback);

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

    public <T extends ZWaveCallback> T requestZWCallback(SerialRequest request, SerialCommand expectedCallbackCommand, long timeout)
            throws SerialException {
        try {
            initExecutionContext(expectedCallbackCommand);
            futureCommand = new CompletableFuture<>();
            controller.requestResponseFlow(request);
            return (T) futureCommand.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new FlowException("Request failed due to timeout");
        } catch (Exception e) {
            throw new FlowException("Unexpected exception occurred");
        } finally {
            cancelExecutionContext();
        }
    }

    private void handleSerialResponse(ImmutableBuffer responseBuffer) {
    }

    private void interceptSerialCallback(ZWaveCallback callback) {
        if (callback.getSerialCommand() == expectedSerialCommand) {
            futureCommand.complete(callback);
        } else {
            log.info("Skipped serial callback: %s", callback.asFineString());
        }
    }

    private void initExecutionContext(SerialCommand serialCommand) {
        this.expectedSerialCommand = serialCommand;
    }

    private void cancelExecutionContext() {
        this.expectedSerialCommand = null;
    }
}
