package com.rposcro.jwavez.serial.probe.model;

import com.rposcro.jwavez.core.model.NodeId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteLink {

  private NodeId routeFrom;
  private NodeId routeTo;
}
