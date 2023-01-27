package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Pattern;

@JsonIgnoreType
public class EndPointAddress {

    private final static Pattern ADDRESS_PATTERN = Pattern.compile("[0-9]+-[0-9]");

    private String address;

    public EndPointAddress(String address) {
        this.address = address;
    }

    public EndPointAddress(byte nodeId, byte endPointId) {
        this.address = (nodeId & 0xff) + "-" + (endPointId & 0xff);
    }

    @JsonValue
    public String getAddress() {
        return address;
    }

    @JsonIgnore
    public int getNodeId() {
        return Integer.parseInt(address.substring(0, address.indexOf('-')));
    }

    @JsonIgnore
    public int getEndPointId() {
        return Integer.parseInt(address.substring(address.indexOf('-') + 1));
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof EndPointAddress
                && ((EndPointAddress) other).address.equals(this.address);
    }

    @Override
    public int hashCode() {
        return this.address.hashCode();
    }

    public static boolean isCorrectAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }
}
