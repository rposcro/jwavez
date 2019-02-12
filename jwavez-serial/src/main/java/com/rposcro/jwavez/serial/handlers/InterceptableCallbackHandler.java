package com.rposcro.jwavez.serial.handlers;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.Callback;
import com.rposcro.jwavez.serial.interceptors.CallbackInterceptor;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterceptableCallbackHandler implements Consumer<ViewBuffer> {

  private InboundFrameValidator validator;
  private InboundFrameParser parser;

  private List<CallbackInterceptor> interceptors;

  public InterceptableCallbackHandler(InboundFrameValidator validator, InboundFrameParser parser) {
    this.validator = validator;
    this.parser = parser;
    this.interceptors = new ArrayList<>();
  }

  @Override
  public void accept(ViewBuffer frameBuffer) {
    if (frameBuffer.get(FRAME_OFFSET_TYPE) != TYPE_REQ || !validator.validate(frameBuffer)) {
      log.warn("Frame validation failed: {}", BufferUtil.bufferToString(frameBuffer));
    }
    try {
      Callback callback = parser.parseCallbackFrame(frameBuffer);
      interceptors.forEach(interceptor -> interceptor.intercept(callback));
    } catch(FrameParseException e) {
      log.warn("Frame parse failed: {}", BufferUtil.bufferToString(frameBuffer));
    }
  }

  public InterceptableCallbackHandler addInterceptor(CallbackInterceptor interceptor) {
    interceptors.add(interceptor);
    return this;
  }
}
