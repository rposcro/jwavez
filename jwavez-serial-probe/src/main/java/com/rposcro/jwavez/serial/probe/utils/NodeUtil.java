package com.rposcro.jwavez.serial.probe.utils;

import static com.rposcro.jwavez.serial.probe.utils.FieldUtil.asInt;

import com.rposcro.jwavez.core.classes.BasicDeviceClass;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.model.NodeInfo;

public class NodeUtil {

  public static NodeInfo decodeNodeInfo(byte[] buffer, int offset) {
    NodeId nodeId = new NodeId(buffer[offset]);
    int length = asInt(buffer[offset + 1]);
    BasicDeviceClass basicDeviceClass = BasicDeviceClass.ofCode(buffer[offset + 2]);
    GenericDeviceClass genericDeviceClass = GenericDeviceClass.ofCode(buffer[offset + 3]);
    SpecificDeviceClass specificDeviceClass = SpecificDeviceClass.ofCode(buffer[offset + 4], genericDeviceClass);
    int cmdClassLen = length - 3;
    CommandClass[] commandClasses = new CommandClass[cmdClassLen];

    for (int i = 0; i < cmdClassLen; i++) {
      commandClasses[i] = CommandClass.ofCode(buffer[offset + 5 + i]);
    }

    return NodeInfo.builder()
        .id(nodeId)
        .basicDeviceClass(basicDeviceClass)
        .genericDeviceClass(genericDeviceClass)
        .specificDeviceClass(specificDeviceClass)
        .commandClasses(commandClasses)
        .build();
  }
}
