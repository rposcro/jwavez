package com.rposcro.jwavez.tools.shell.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongleNetworkInformation {

    private long networkId;
    private int dongleNodeId;
    private int sucNodeId;
    private int[] nodeIds;
}
