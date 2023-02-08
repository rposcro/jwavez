package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.helpers.RequestCallbackFlowHelper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.FlowCallback;
import com.rposcro.jwavez.serial.frames.responses.SolicitedCallbackResponse;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.handlers.LastResponseHolder;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.rposcro.jwavez.serial.utils.BufferUtil.bufferToString;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralAsynchronousController extends AbstractAsynchronousController<GeneralAsynchronousController> {

    private static final long DEFAULT_CALLBACK_TIMEOUT_MILLIS = 5000;

    private Semaphore controllerLock;

    private long timeoutMillis;
    private InboundFrameParser parser;
    private InboundFrameValidator validator;
    private RequestCallbackFlowHelper callbackFlowHelper;
    private LastResponseHolder lastResponseHandler;
    private Optional<ResponseHandler> customResponseHandler;
    private Optional<CallbackHandler> customCallbackHandler;

    private byte expectedCallbackFlowId;
    private byte expectedCommandCode;
    private CompletableFuture<FlowCallback> expectedFutureCallback;

    public <T extends ZWaveResponse> T requestResponseFlow(SerialRequest request) throws RxTxException, FrameException {
        try {
            controllerLock.acquireUninterruptibly();
            return doRequest(request);
        } finally {
            controllerLock.release();
        }
    }

    public <T extends FlowCallback> T requestCallbackFlow(SerialRequest request) throws FlowException, RxTxException, FrameException {
        try {
            controllerLock.acquireUninterruptibly();
            callbackFlowHelper.solicitedCallbackResponseClass(request.getSerialCommand());
            expectedCallbackFlowId = request.getCallbackFlowId();
            expectedCommandCode = request.getSerialCommand().getCode();
            expectedFutureCallback = new CompletableFuture<>();

            SolicitedCallbackResponse response = doRequest(request);
            if (request.isResponseExpected() && !response.isSolicitedCallbackToFollow()) {
                throw new FlowException("Received response is not expecting callback to follow!");
            }

            return (T) expectedFutureCallback.get(DEFAULT_CALLBACK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
            log.error("Failed to execute request-response flow!", e);
            throw new FlowException(e);
        } finally {
            expectedFutureCallback = null;
            controllerLock.release();
        }
    }

    private void handleResponse(ViewBuffer frameBuffer) {
        lastResponseHandler.accept(frameBuffer);

        if (log.isDebugEnabled()) {
            log.debug("ZWaveResponse response frame received: {}", bufferToString(frameBuffer));
            log.debug(FrameUtil.asFineString(frameBuffer));
        }

        customResponseHandler.ifPresent(handler -> handler.accept(frameBuffer));
    }

    private void handleCallback(ViewBuffer frameBuffer) {
        if (expectedFutureCallback != null && !expectedFutureCallback.isDone() && frameBuffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND) == expectedCommandCode) {
            if (validator.validate(frameBuffer)) {
                try {
                    FlowCallback callback = (FlowCallback) parser.parseCallbackFrame(frameBuffer);

                    if (log.isDebugEnabled()) {
                        log.debug("ZWaveResponse response frame received: {}", bufferToString(frameBuffer));
                        log.debug(FrameUtil.asFineString(frameBuffer));
                    }

                    if (callback.getCallbackFlowId() == expectedCallbackFlowId) {
                        expectedFutureCallback.complete(callback);
                    } else {
                        log.info("Received matching callback class but flow id differs from expected: {}", callback.getCallbackFlowId());
                    }
                } catch (FrameParseException e) {
                    log.error("Callback frame parsing failed {}", BufferUtil.bufferToString(frameBuffer));
                }
            } else {
                log.warn("Received callback frame which failed validation {}", BufferUtil.bufferToString(frameBuffer));
            }
        }
        customCallbackHandler.ifPresent(handler -> handler.accept(frameBuffer));
    }

    private <T extends ZWaveResponse> T doRequest(SerialRequest request) throws RxTxException, FrameException {
        rxTxRouterProcess.sendRequest(request);
        if (request.isResponseExpected()) {
            return (T) lastResponseHandler.get();
        } else {
            return null;
        }
    }

    @Builder
    private static GeneralAsynchronousController build(
            @NonNull String dongleDevice,
            long timeoutMillis,
            RxTxConfiguration rxTxConfiguration,
            ExecutorService executorService,
            ResponseHandler responseHandler,
            CallbackHandler callbackHandler) {

        GeneralAsynchronousController instance = new GeneralAsynchronousController();
        instance.helpWithBuild(dongleDevice, rxTxConfiguration, instance::handleResponse, instance::handleCallback, executorService);

        instance.customResponseHandler = Optional.ofNullable(responseHandler);
        instance.customCallbackHandler = Optional.ofNullable(callbackHandler);
        instance.lastResponseHandler = new LastResponseHolder();
        instance.callbackFlowHelper = RequestCallbackFlowHelper.defaultHelper();
        instance.parser = InboundFrameParser.defaultParser();
        instance.validator = InboundFrameValidator.defaultValidator();
        instance.controllerLock = new Semaphore(1);
        instance.timeoutMillis = timeoutMillis;
        return instance;
    }
}
