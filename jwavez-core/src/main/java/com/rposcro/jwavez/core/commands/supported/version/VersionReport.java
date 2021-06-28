package com.rposcro.jwavez.core.commands.supported.version;

import com.rposcro.jwavez.core.commands.enums.VersionCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.enums.ZWaveLibraryType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VersionReport extends ZWaveSupportedCommand<VersionCommandType> {

    private final short zWaveLibraryType;
    private final short zWaveProtocolVersion;
    private final short zWaveProtocolSubVersion;
    private final short applicationVersion;
    private final short applicationSubVersion;

    public VersionReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(VersionCommandType.VERSION_REPORT, sourceNodeId);
        payload.skip(2);
        zWaveLibraryType = payload.nextUnsignedByte();
        zWaveProtocolVersion = payload.nextUnsignedByte();
        zWaveProtocolSubVersion = payload.nextUnsignedByte();
        applicationVersion = payload.nextUnsignedByte();
        applicationSubVersion = payload.nextUnsignedByte();
    }

    public ZWaveLibraryType getZWaveLibraryTypeEnum() {
        return ZWaveLibraryType.ofCodeOptional((byte) zWaveLibraryType).orElse(null);
    }
}
