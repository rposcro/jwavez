package com.rposcro.jwavez.serial.handlers;

import static com.rposcro.jwavez.serial.utils.BufferUtil.bufferToString;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LastCallbackHolder implements Consumer<ViewBuffer> {

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
  public void accept(ViewBuffer frameBuffer) {
    if (log.isDebugEnabled()) {
      log.debug("ZWaveCallback frame received: {}", bufferToString(frameBuffer));
    }

    lastException = null;
    lastCallback = null;

    if (!validator.validate(frameBuffer)) {
      lastException = new FrameException("Inbound frame validation failed: {}", BufferUtil.bufferToString(frameBuffer));
    }

    try {
      lastCallback = parser.parseCallbackFrame(frameBuffer);
    } catch(FrameParseException e) {
      this.lastException = e;
    }
  }
}
