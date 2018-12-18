package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OutboundOrder {

  private SOFFrame outboundFrame;
  private Optional<Object> orderMarker;
}
