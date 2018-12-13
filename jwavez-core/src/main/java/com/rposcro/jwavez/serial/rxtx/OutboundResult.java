package com.rposcro.jwavez.serial.rxtx;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OutboundResult {

  private Optional<Object> orderMarker;
  private boolean success;
}
