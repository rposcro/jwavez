package com.rposcro.jwavez.model;

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
