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
    private byte[] colorComponents;
    private byte switchDuration;

    public SwitchColorSet(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_SET, sourceNodeId);
        payload.skip(2);
        colorComponentCount = (short) (0b00011111 & payload.nextUnsignedByte());
        colorComponents = new byte[colorComponentCount * 2];

        for (int i = 0; i < colorComponentCount; ) {
            colorComponents[i++] = payload.nextByte();
            colorComponents[i++] = payload.nextByte();
        }

        if (payload.hasNext()) {
            commandVersion = 2;
            switchDuration = payload.nextByte();
        } else {
            commandVersion = 1;
        }
    }

    public Map<ColorComponent, Byte> getColorComponentsMap() {
        HashMap<ColorComponent, Byte> componentsMap = new HashMap<>();
        for (int i = 0; i < colorComponentCount; i++) {
            byte componentId = colorComponents[i * 2];
            byte componentValue = colorComponents[i * 2 + 1];
            ColorComponent.ofCodeOptional(componentId).ifPresent(component -> {
                componentsMap.put(component, componentValue);
            });
        }
        return componentsMap;
    }
}
