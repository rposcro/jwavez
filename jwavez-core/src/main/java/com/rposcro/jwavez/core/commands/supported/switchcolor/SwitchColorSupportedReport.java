package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BytesUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SwitchColorSupportedReport extends ZWaveSupportedCommand<SwitchColorCommandType> {

    private byte[] colorComponentsMask;

    public SwitchColorSupportedReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT, sourceNodeId);
        payload.skip(2);

        colorComponentsMask = new byte[payload.available()];
        for (int i = 0; i < colorComponentsMask.length; i++) {
            colorComponentsMask[i] = payload.nextByte();
        }

        commandVersion = 1;
    }

    public List<ColorComponent> getColorComponents() {
        List<ColorComponent> colorComponents = new ArrayList<>();

        for (int chunkIdx = 0; chunkIdx < colorComponentsMask.length; chunkIdx++) {
            byte chunk = colorComponentsMask[chunkIdx];
            int bitMask = 1;

            for (int bitIdx = 0; bitIdx < 8; bitIdx++) {
                if ((chunk & bitMask) != 0) {
                    ColorComponent.ofCodeOptional((byte) (chunkIdx * 8 + bitIdx)).ifPresent(colorComponents::add);
                }
                bitMask <<= 1;
            }
        }

        return colorComponents;
    }

    @Override
    public String asNiceString() {
        return String.format("%s components[%s]",
                super.asNiceString(), BytesUtil.asString(colorComponentsMask)
        );
    }
}
