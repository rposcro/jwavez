package com.rposcro.jwavez.core.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class NodeId {

    private final static NodeId[] CACHED = new NodeId[255];

    private byte id;

    public NodeId(int id) {
        if (id < 0 || id > 255) {
            throw new IllegalArgumentException("Node id has to be in range of 0 to 255");
        }
        this.id = (byte) id;
    }

    @Override
    public String toString() {
        return "NodeId<" + id + ">";
    }

    public static NodeId forId(byte id) {
        return forId(id & 0xff);
    }

    public static NodeId forId(int id) {
        if (CACHED[id] == null) {
            CACHED[id] = new NodeId(id);
        }
        return CACHED[id];
    }
}
