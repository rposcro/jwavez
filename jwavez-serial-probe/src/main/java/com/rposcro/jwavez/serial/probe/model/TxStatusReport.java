package com.rposcro.jwavez.serial.probe.model;

import com.rposcro.jwavez.core.model.NodeId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TxStatusReport {

  private int transmitTicksCount;
  private short ackChannelNumber;
  private short lastTxChannelNumber;

  private short repeatersInRouteCount;
  private NodeId[] repeaters;
  private short routeTriesCount;
  private short routeSpeed;

  private RouteLink lastFailedLink;

  private byte routeSchemeState;
  private long rssiValues;
}
