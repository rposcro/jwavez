package com.rposcro.jwavez.core.model;

import com.rposcro.jwavez.core.enums.BasicDeviceClass;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.enums.GenericDeviceClass;
import com.rposcro.jwavez.core.enums.SpecificDeviceClass;
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
