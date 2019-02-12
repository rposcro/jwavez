package com.rposcro.jwavez.serial.handlers;

import static com.rposcro.jwavez.serial.utils.BufferUtil.bufferToString;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LastResponseHolder implements Consumer<ViewBuffer> {

  private InboundFrameParser parser;
  private InboundFrameValidator validator;

  private ZWaveResponse lastResponse;
  private FrameException lastException;

  public LastResponseHolder() {
    this.validator = new InboundFrameValidator();
    this.parser = new InboundFrameParser();
  }

  public ZWaveResponse get() throws FrameException {
    try {
      if (lastException != null) {
        throw lastException;
      }
      return lastResponse;
    } finally {
      lastException = null;
      lastResponse = null;
    }
  }

  @Override
  public void accept(ViewBuffer frameBuffer) {
    if (log.isDebugEnabled()) {
      log.debug("ZWaveResponse frame received: {}", bufferToString(frameBuffer));
    }

    lastException = null;
    lastResponse = null;

    if (!validator.validate(frameBuffer)) {
      lastException = new FrameException("Inbound frame validation failed: {}", BufferUtil.bufferToString(frameBuffer));
    }

    try {
      lastResponse = parser.parseResponseFrame(frameBuffer);
    } catch(FrameParseException e) {
      this.lastException = e;
    }
  }
}
