package com.rposcro.jwavez.serial.controllers.builders;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.AssertUtil;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionState;
import com.rposcro.jwavez.serial.controllers.inclusion.AbstractFlowHandler;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.handlers.InterceptableResponseHandler;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class AbstractInclusionControllerBuilder<S extends TransactionState, T extends AbstractInclusionControllerBuilder> extends AbstractAsynchronousControllerBuilder<T> {

    private long waitForTouchTimeout;
    private long waitForProgressTimeout;

    private TransactionKeeper<S> transactionKeeper;
    private AbstractFlowHandler flowHandler;

    public T waitForTouchTimeout(long waitForTouchTimeout) {
        this.waitForTouchTimeout = waitForTouchTimeout;
        return (T) this;
    }

    public T waitForProgressTimeout(long waitForProgressTimeout) {
        this.waitForProgressTimeout = waitForProgressTimeout;
        return (T) this;
    }

    protected void ensureBuildReadiness(TransactionKeeper<S> transactionKeeper, AbstractFlowHandler flowHandler) {
        AssertUtil.nonNull(transactionKeeper, "TransactionKeeper cannot be null!");
        AssertUtil.nonNull(flowHandler, "FlowHandler cannot be null!");

        this.transactionKeeper = transactionKeeper;
        this.flowHandler = flowHandler;

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        callbackHandler.addCallbackInterceptor(flowHandler::handleCallback);
        callbackHandler.addFrameBufferInterceptor(this::logCallback);

        InterceptableResponseHandler responseHandler = new InterceptableResponseHandler();
        responseHandler.addFrameBufferInterceptor(this::logResponse);

        super.ensureBuildReadiness(responseHandler, callbackHandler);
    }

    public void logCallback(ImmutableBuffer frameBuffer) {
        if (log.isDebugEnabled()) {
            log.debug("Callback frame received: {}", BuffersUtil.asString(frameBuffer));
            log.debug(FramesUtil.asFineString(frameBuffer));
        }
    }

    public void logResponse(ImmutableBuffer frameBuffer) {
        if (log.isDebugEnabled()) {
            log.debug("Response frame received: {}", BuffersUtil.asString(frameBuffer));
            log.debug(FramesUtil.asFineString(frameBuffer));
        }
    }
}
