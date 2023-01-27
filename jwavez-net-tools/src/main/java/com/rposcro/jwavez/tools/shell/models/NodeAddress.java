package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Pattern;

@JsonIgnoreType
public class NodeAddress {

    private final static Pattern ADDRESS_PATTERN = Pattern.compile("[0-9]+");

    private byte nodeId;

    public NodeAddress(String address) {
        this.nodeId = (byte) Short.parseShort(address);
    }

    public NodeAddress(byte nodeId) {
        this.nodeId = nodeId;
    }

    @JsonValue
    public String getAddress() {
        return "" + (((int) nodeId) & 0xff);
    }

    @JsonIgnore
    public byte getNodeId() {
        return this.nodeId;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof NodeAddress
                && ((NodeAddress) other).nodeId == this.nodeId;
    }

    @Override
    public int hashCode() {
        return this.nodeId;
    }

    public static boolean isCorrectAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }
}
