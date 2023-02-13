package com.rposcro.jwavez.core.commands.controlled.builders.configuration;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.model.BitLength;
import com.rposcro.jwavez.core.utils.BytesUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class ConfigurationCommandBuilderV1 {

    public ZWaveControlledCommand buildSetByteParameterCommand(int parameterNumber, byte value) {
        return buildSetParameterCommand(parameterNumber, value & 0xff, BitLength.BIT_LENGTH_8);
    }

    public ZWaveControlledCommand buildSetParameterCommand(int parameterNumber, int value, BitLength valueSize) {
        byte[] payload = new byte[4 + valueSize.getBytesNumber()];
        payload[0] = CommandClass.CMD_CLASS_CONFIGURATION.getCode();
        payload[1] = ConfigurationCommandType.CONFIGURATION_SET.getCode();
        payload[2] = (byte) parameterNumber;
        payload[3] = sizeField(valueSize.getBytesNumber(), false);
        BytesUtil.writeMSBValue(payload, 4, valueSize, value);
        return new ZWaveControlledCommand(payload);
    }

    public ZWaveControlledCommand buildGetParameterCommand(int parameterNumber) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
                ConfigurationCommandType.CONFIGURATION_GET.getCode(),
                (byte) parameterNumber);
    }

    protected static byte sizeField(int valueSize, boolean resetToDefault) {
        return (byte) ((valueSize & 0x7) | (resetToDefault ? 0x80 : 0));
    }
}
