package com.rposcro.jwavez.tools.utils;

import com.rposcro.jwavez.serial.exceptions.SerialException;

@FunctionalInterface
public interface SerialProducer<R> {

    R execute() throws SerialException;
}
