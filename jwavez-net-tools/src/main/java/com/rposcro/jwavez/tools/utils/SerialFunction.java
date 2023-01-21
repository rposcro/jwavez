package com.rposcro.jwavez.tools.utils;

import com.rposcro.jwavez.serial.exceptions.SerialException;

@FunctionalInterface
public interface SerialFunction<A, R> {

    R execute(A argument) throws SerialException;
}
