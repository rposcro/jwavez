package com.rposcro.jwavez.tools.shell.models;

import com.rposcro.jwavez.core.classes.BasicDeviceClass;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeDetails {

    private int nodeId;
    private String nodeMemo;

    private int manufacturerId;
    private int productTypeId;
    private int productId;

    private short zWaveLibraryType;
    private short zWaveProtocolVersion;
    private short zWaveProtocolSubVersion;
    private short applicationVersion;
    private short applicationSubVersion;

    private BasicDeviceClass basicDeviceClass;
    private GenericDeviceClass genericDeviceClass;
    private SpecificDeviceClass specificDeviceClass;
    private CommandClassMeta[] commandClasses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommandClassMeta {
        private CommandClass commandClass;
        private int version;
    }
}