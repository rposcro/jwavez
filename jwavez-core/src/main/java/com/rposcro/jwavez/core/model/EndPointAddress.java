package com.rposcro.jwavez.core.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class EndPointAddress implements Comparable<EndPointAddress> {

    private byte nodeId;
    private byte endPointId;

    public EndPointAddress(int nodeId, int endPointId) {
        if (nodeId < 0 || nodeId > 255) {
            throw new IllegalArgumentException("Node id has to be in range of 0 to 255");
        }
        if (endPointId < 0 || endPointId > 255) {
            throw new IllegalArgumentException("EndPoint id has to be in range of 0 to 255");
        }

        this.nodeId = (byte) nodeId;
        this.endPointId = (byte) endPointId;
    }

    public EndPointAddress(byte nodeId, byte endPointId) {
        this.nodeId = nodeId;
        this.endPointId = endPointId;
    }

    @Override
    public int compareTo(EndPointAddress other) {
        int nodeDiff = Byte.toUnsignedInt(nodeId) - Byte.toUnsignedInt(other.nodeId);
        return nodeDiff != 0 ? nodeDiff : Byte.toUnsignedInt(endPointId) - Byte.toUnsignedInt(other.endPointId);
    }
}
