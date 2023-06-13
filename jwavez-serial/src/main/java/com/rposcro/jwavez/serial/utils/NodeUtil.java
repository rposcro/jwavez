package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.BasicDeviceClass;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.model.NodeInfo;

import java.util.ArrayList;
import java.util.List;

public class NodeUtil {

    public static NodeInfo decodeNodeInfo(ImmutableBuffer frameBuffer) {
        NodeId nodeId = new NodeId(frameBuffer.nextByte());
        int length = FieldUtil.asInt(frameBuffer.nextByte());
        BasicDeviceClass basicDeviceClass = BasicDeviceClass.ofCode(frameBuffer.nextByte());
        GenericDeviceClass genericDeviceClass = GenericDeviceClass.ofCode(frameBuffer.nextByte());
        SpecificDeviceClass specificDeviceClass = SpecificDeviceClass.ofCode(frameBuffer.nextByte(), genericDeviceClass);
        int cmdClassLen = length - 3;

        List<CommandClass> commandClasses = new ArrayList<>(cmdClassLen);
        byte[] commandCodes = new byte[cmdClassLen];

        for (int i = 0; i < cmdClassLen; i++) {
            commandCodes[i] = frameBuffer.nextByte();
            CommandClass.optionalOfCode(commandCodes[i])
                    .ifPresent(commandClasses::add);
        }

        return NodeInfo.builder()
                .id(nodeId)
                .basicDeviceClass(basicDeviceClass)
                .genericDeviceClass(genericDeviceClass)
                .specificDeviceClass(specificDeviceClass)
                .commandClasses(commandClasses.toArray(new CommandClass[0]))
                .commandCodes(commandCodes)
                .build();
    }
}
