package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.function.Consumer;

public interface ResponseHandler extends Consumer<ImmutableBuffer> {
}
