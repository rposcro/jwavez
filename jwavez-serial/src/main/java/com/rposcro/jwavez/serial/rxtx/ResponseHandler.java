package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;

import java.util.function.Consumer;

public interface ResponseHandler extends Consumer<ViewBuffer> {
}
