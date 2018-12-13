package com.rposcro.jwavez.model;

import com.rposcro.jwavez.enums.BasicDeviceClass;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.enums.GenericDeviceClass;
import com.rposcro.jwavez.enums.SpecificDeviceClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NodeInfo {

  private NodeId id;
  private BasicDeviceClass basicDeviceClass;
  private GenericDeviceClass genericDeviceClass;
  private SpecificDeviceClass specificDeviceClass;
  private CommandClass[] commandClasses;

}
