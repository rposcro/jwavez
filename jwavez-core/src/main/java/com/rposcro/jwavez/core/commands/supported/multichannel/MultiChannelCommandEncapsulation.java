package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BytesUtil;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelCommandEncapsulation extends ZWaveSupportedCommand<MultiChannelCommandType> {

    private byte sourceEndpointId;
    private byte destinationEndpointId;
    private boolean destinationAsBitMask;
    private CommandClass encapsulatedCommandClass;
    private byte encapsulatedCommandCode;
    private byte[] encapsulatedCommandPayload;

    public MultiChannelCommandEncapsulation(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, sourceNodeId);
        payload.skip(2);

        sourceEndpointId = (byte) (payload.next() & 0x7f);
        destinationEndpointId = payload.next();
        destinationAsBitMask = (destinationEndpointId & 0x80) != 0;
        destinationEndpointId = (byte) (destinationEndpointId & 0x7f);

        short cmdClassCode = payload.nextUnsignedByte();
        encapsulatedCommandClass = CommandClass.optionalOfCode((byte) cmdClassCode).orElse(CommandClass.CMD_CLASS_UNKNOWN);
        if (cmdClassCode >= 0xf1) {
            // extended command class, need to skip next byte
            payload.skip(1);
        }
        encapsulatedCommandCode = payload.next();

        int payloadSize = payload.available() + 2;
        encapsulatedCommandPayload = new byte[payloadSize];
        encapsulatedCommandPayload[0] = (byte) cmdClassCode;
        encapsulatedCommandPayload[1] = encapsulatedCommandCode;
        for (int i = 2; i < payloadSize; i++) {
            encapsulatedCommandPayload[i] = payload.next();
        }
    }

    @Override
    public String asNiceString() {
        return String.format("%s sourceEndpointId(%02x) destinationEndpointId(%02x)"
                        + " destinationAsBitMask(%s) encapsulatedCommandClass(%s) encapsulatedCommandCode(%02x)"
                        + " encapsulatedPayload[%s]",
                super.asNiceString(),
                sourceEndpointId,
                destinationEndpointId,
                destinationAsBitMask,
                encapsulatedCommandClass,
                encapsulatedCommandCode,
                BytesUtil.arrayToString(encapsulatedCommandPayload)
        );
    }
}
