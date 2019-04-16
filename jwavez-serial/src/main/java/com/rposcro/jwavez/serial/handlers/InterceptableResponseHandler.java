package com.rposcro.jwavez.serial.handlers;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_RES;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.interceptors.ResponseInterceptor;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterceptableResponseHandler implements Consumer<ViewBuffer> {

  private InboundFrameValidator validator;
  private InboundFrameParser parser;

  private List<ResponseInterceptor> interceptors;

  public InterceptableResponseHandler() {
    this.validator = new InboundFrameValidator();
    this.parser = new InboundFrameParser();
    this.interceptors = new ArrayList<>();
  }

  @Override
  public void accept(ViewBuffer frameBuffer) {
    if (frameBuffer.get(FRAME_OFFSET_TYPE) != TYPE_RES || !validator.validate(frameBuffer)) {
      log.warn("Frame validation failed: {}", BufferUtil.bufferToString(frameBuffer));
    } else if (log.isDebugEnabled()) {
      log.debug("Frame received: {}", BufferUtil.bufferToString(frameBuffer));
    }

    try {
      ZWaveResponse response = parser.parseResponseFrame(frameBuffer);
      interceptors.forEach(interceptor -> interceptor.intercept(response));
    } catch(FrameParseException e) {
      log.warn("Frame parse failed: {}", BufferUtil.bufferToString(frameBuffer));
    }
  }

  public InterceptableResponseHandler addInterceptor(ResponseInterceptor interceptor) {
    interceptors.add(interceptor);
    return this;
  }
}
