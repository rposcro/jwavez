package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SwitchColorSupportedReport extends ZWaveSupportedCommand<SwitchColorCommandType> {

    private short colorComponentMask1;
    private short colorComponentMask2;
    private List<ColorComponent> colorComponents;

    public SwitchColorSupportedReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT, sourceNodeId);
        payload.skip(2);
        colorComponentMask1 = payload.nextUnsignedByte();
        colorComponentMask2 = payload.nextUnsignedByte();
    }

    public List<ColorComponent> getColorComponents() {
        if (colorComponents == null) {
            int bitMask = 1;
            int mask = colorComponentMask1 | (colorComponentMask2 << 8);
            this.colorComponents = new ArrayList<>();
            for (byte i = 0; i < 16; i++) {
                if ((mask & bitMask) != 0) {
                    ColorComponent.ofCodeOptional(i).ifPresent(colorComponents::add);
                }
                bitMask <<= 1;
            }
        }
        return colorComponents;
    }

    @Override
    public String toString() {
        return SwitchColorSupportedReport.class.getName()
                + "<component masks: " + colorComponentMask1 + ", " + colorComponentMask2 + ">";
    }
}
