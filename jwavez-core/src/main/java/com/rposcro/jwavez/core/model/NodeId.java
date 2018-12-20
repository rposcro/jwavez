package com.rposcro.jwavez.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeId {

  private byte id;

  @Override
  public String toString() {
    return "NodeId<" + id + ">";
  }
}
