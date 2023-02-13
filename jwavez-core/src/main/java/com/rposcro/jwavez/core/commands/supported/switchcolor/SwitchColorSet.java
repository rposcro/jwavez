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
    private Map<ColorComponent, Byte> colorComponentsMap;

    public SwitchColorSet(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_SET, sourceNodeId);
        payload.skip(2);
        colorComponentCount = (short) (0b00011111 & payload.nextUnsignedByte());
        colorComponentsMap = new HashMap<>();

        for (int i = 0; i < colorComponentCount; i++) {
            byte componentId = payload.nextByte();
            byte componentValue = payload.nextByte();
            ColorComponent.ofCodeOptional(componentId).ifPresent(component -> {
                colorComponentsMap.put(component, componentValue);
            });
        }
    }
}
