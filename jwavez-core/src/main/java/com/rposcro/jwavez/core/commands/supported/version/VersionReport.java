package com.rposcro.jwavez.core.commands.supported.version;

import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.ZWaveLibraryType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VersionReport extends ZWaveSupportedCommand<VersionCommandType> {

    private short zWaveLibraryType;
    private short zWaveProtocolVersion;
    private short zWaveProtocolSubVersion;
    private short applicationVersion;
    private short applicationSubVersion;
    private short hardwareVersion;
    private short[][] firmwareVersions;

    public VersionReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(VersionCommandType.VERSION_REPORT, sourceNodeId);
        payload.skip(2);
        zWaveLibraryType = payload.nextUnsignedByte();
        zWaveProtocolVersion = payload.nextUnsignedByte();
        zWaveProtocolSubVersion = payload.nextUnsignedByte();
        applicationVersion = payload.nextUnsignedByte();
        applicationSubVersion = payload.nextUnsignedByte();

        if (!payload.hasNext()) {
            commandVersion = 1;
        } else {
            commandVersion = 2;
            hardwareVersion = payload.nextUnsignedByte();
            int firmwareVersionsCount = 1 + payload.nextUnsignedByte();
            firmwareVersions = new short[firmwareVersionsCount][2];
            firmwareVersions[0][0] = applicationVersion;
            firmwareVersions[0][1] = applicationSubVersion;

            for (int i = 1; i < firmwareVersionsCount; i++) {
                firmwareVersions[i][0] = payload.nextUnsignedByte();
                firmwareVersions[i][1] = payload.nextUnsignedByte();
            }
        }
    }

    public ZWaveLibraryType getDecodedZWaveLibraryType() {
        return ZWaveLibraryType.ofCodeOptional((byte) zWaveLibraryType).orElse(null);
    }
}
