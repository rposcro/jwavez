package com.rposcro.jwavez.serial.probe.rxtx;

import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OutboundOrder {

  private SOFFrame outboundFrame;
  private Optional<Object> orderMarker;
}
