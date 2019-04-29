package com.rposcro.jwavez.tools.cli.utils;

import com.rposcro.jwavez.serial.exceptions.SerialException;

@FunctionalInterface
public interface SerialProcedure {

  void execute() throws SerialException;
}
