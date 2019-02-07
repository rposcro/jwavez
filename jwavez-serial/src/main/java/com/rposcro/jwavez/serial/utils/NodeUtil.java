package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.core.enums.BasicDeviceClass;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.enums.GenericDeviceClass;
import com.rposcro.jwavez.core.enums.SpecificDeviceClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;

public class NodeUtil {

  public static NodeInfo decodeNodeInfo(ViewBuffer frameBuffer) {
    NodeId nodeId = new NodeId(frameBuffer.get());
    int length = FieldUtil.asInt(frameBuffer.get());
    BasicDeviceClass basicDeviceClass = BasicDeviceClass.ofCode(frameBuffer.get());
    GenericDeviceClass genericDeviceClass = GenericDeviceClass.ofCode(frameBuffer.get());
    SpecificDeviceClass specificDeviceClass = SpecificDeviceClass.ofCode(frameBuffer.get(), genericDeviceClass);
    int cmdClassLen = length - 3;
    CommandClass[] commandClasses = new CommandClass[cmdClassLen];

    for (int i = 0; i < cmdClassLen; i++) {
      commandClasses[i] = CommandClass.ofCode(frameBuffer.get());
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
