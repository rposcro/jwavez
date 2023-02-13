package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

import java.util.stream.IntStream;

@Getter
@ToString
public class MultiChannelCapabilityReport extends ZWaveSupportedCommand<MultiChannelCommandType> {

    private boolean endPointDynamic;
    private byte endPointId;
    private byte genericDeviceClass;
    private byte specificDeviceClass;
    private byte[] commandClasses;

    public MultiChannelCapabilityReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_REPORT, sourceNodeId);
        payload.skip(2);
        byte endpoint = payload.next();
        endPointDynamic = (endpoint & 0x80) != 0;
        endPointId = (byte) (endpoint & 0x7F);
        genericDeviceClass = payload.next();
        specificDeviceClass = payload.next();

        int commandClassCount = payload.available();
        commandClasses = new byte[commandClassCount];
        for (int i = 0; i < commandClassCount; i++) {
            commandClasses[i] = payload.next();
        }

        commandVersion = 3;
    }

    public GenericDeviceClass getDecodedGenericDeviceClass() {
        return GenericDeviceClass.ofCodeOptional(this.genericDeviceClass).orElse(null);
    }

    public SpecificDeviceClass getDecodedSpecificDeviceClass() {
        return SpecificDeviceClass.ofCodeOptional(this.specificDeviceClass, getDecodedGenericDeviceClass())
                .orElse(null);
    }

    public CommandClass[] getDecodedCommandClasses() {
        CommandClass[] classes = (CommandClass[]) IntStream.range(0, commandClasses.length)
                .mapToObj(idx -> CommandClass.optionalOfCode(commandClasses[idx]))
                .map(opt -> opt.orElse(CommandClass.CMD_CLASS_UNKNOWN))
                .toArray();
        return classes;
    }
}
