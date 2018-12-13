package com.rposcro.jwavez.commands.controlled;

public interface ZWaveControlledCommand {

  byte[] getPayloadBuffer();
  int getPayloadLength();
}
