package com.rposcro.jwavez.serial.probe.builders;

import com.rposcro.jwavez.serial.probe.frame.constants.TransmitOption;

public class TransmitOptionsBuilder {

  private byte options;

  public TransmitOptionsBuilder withOption(TransmitOption option) {
    options |= option.getCode();
    return this;
  }

  public byte buildOptions() {
    return options;
  }
}
