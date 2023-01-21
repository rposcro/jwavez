package com.rposcro.jwavez.core.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class EndPointId {

    private byte nodeId;
    private byte endPointId;

    public EndPointId(int nodeId, int endPointId) {
        if (nodeId < 0 || nodeId > 255) {
            throw new IllegalArgumentException("Node id has to be in range of 0 to 255");
        }
        if (endPointId < 0 || endPointId > 255) {
            throw new IllegalArgumentException("EndPoint id has to be in range of 0 to 255");
        }

        this.nodeId = (byte) nodeId;
        this.endPointId = (byte) endPointId;
    }
}
