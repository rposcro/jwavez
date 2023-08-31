package com.rposcro.jwavez.core.model;

import com.rposcro.jwavez.core.classes.BasicDeviceClass;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NodeInfo {

    private NodeId id;
    private BasicDeviceClass basicDeviceClass;
    private GenericDeviceClass genericDeviceClass;
    private SpecificDeviceClass specificDeviceClass;
    private CommandClass[] commandClasses;
    private byte[] commandCodes;
}
