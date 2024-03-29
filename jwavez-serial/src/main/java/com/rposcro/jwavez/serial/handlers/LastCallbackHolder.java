package com.rposcro.jwavez.serial.handlers;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;

import com.rposcro.jwavez.serial.utils.FramesUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LastCallbackHolder implements CallbackHandler {

    private InboundFrameParser parser;
    private InboundFrameValidator validator;

    private ZWaveCallback lastCallback;
    private FrameException lastException;

    public LastCallbackHolder() {
        this.validator = new InboundFrameValidator();
        this.parser = new InboundFrameParser();
    }

    public ZWaveCallback get() throws FrameException {
        try {
            if (lastException != null) {
                throw lastException;
            }
            return lastCallback;
        } finally {
            lastException = null;
            lastCallback = null;
        }
    }

    @Override
    public void accept(ImmutableBuffer frameBuffer) {
        lastException = null;
        lastCallback = null;

        if (!validator.validate(frameBuffer)) {
            lastException = new FrameException("Inbound callback frame validation failed: {}", BuffersUtil.asString(frameBuffer));
        }

        try {
            lastCallback = parser.parseCallbackFrame(frameBuffer);
            if (log.isDebugEnabled()) {
                log.debug(FramesUtil.asFineString(frameBuffer));
            }
        } catch (FrameParseException e) {
            this.lastException = e;
        }
    }
}
