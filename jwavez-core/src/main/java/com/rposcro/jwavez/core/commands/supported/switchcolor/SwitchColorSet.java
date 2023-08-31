package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class SwitchColorSet extends ZWaveSupportedCommand<SwitchColorCommandType> {

    private short colorComponentCount;
    private short[][] colorComponents;
    private short switchDuration;

    public SwitchColorSet(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_SET, sourceNodeId);
        payload.skip(2);
        colorComponentCount = (short) (0b00011111 & payload.nextUnsignedByte());
        colorComponents = new short[colorComponentCount][2];

        for (int i = 0; i < colorComponentCount; i++) {
            colorComponents[i][0] = payload.nextUnsignedByte();
            colorComponents[i][1] = payload.nextUnsignedByte();
        }

        if (payload.hasNext()) {
            commandVersion = 2;
            switchDuration = payload.nextUnsignedByte();
        } else {
            commandVersion = 1;
        }
    }

    public Map<ColorComponent, Short> getColorComponentsMap() {
        HashMap<ColorComponent, Short> componentsMap = new HashMap<>();
        for (int i = 0; i < colorComponentCount; i++) {
            byte componentId = (byte) colorComponents[i][0];
            short componentValue = colorComponents[i][1];
            ColorComponent.ofCodeOptional(componentId).ifPresent(component -> {
                componentsMap.put(component, componentValue);
            });
        }
        return componentsMap;
    }
}
