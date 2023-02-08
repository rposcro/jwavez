package com.rposcro.jwavez.core.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class NodeId {

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
}
