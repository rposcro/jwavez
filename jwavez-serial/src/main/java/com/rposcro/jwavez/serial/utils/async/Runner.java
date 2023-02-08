package com.rposcro.jwavez.serial.utils.async;

@FunctionalInterface
public interface Runner<T extends Exception> {

    void run() throws T;
}
