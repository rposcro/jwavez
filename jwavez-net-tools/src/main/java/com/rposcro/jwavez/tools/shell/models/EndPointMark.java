package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Pattern;

@JsonIgnoreType
public class EndPointMark {

    private final static Pattern MARK_PATTERN = Pattern.compile("[0-9]+-[0-9]");

    private String mark;

    public EndPointMark(String mark) {
        this.mark = mark;
    }

    public EndPointMark(byte nodeId, byte endPointId) {
        this.mark = (nodeId & 0xff) + "-" + (endPointId & 0xff);
    }

    @JsonValue
    public String getMark() {
        return mark;
    }

    @JsonIgnore
    public int getNodeId() {
        return Integer.parseInt(mark.substring(0, mark.indexOf('-')));
    }

    @JsonIgnore
    public int getEndPointId() {
        return Integer.parseInt(mark.substring(mark.indexOf('-') + 1));
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof EndPointMark
                && ((EndPointMark) other).mark.equals(this.mark);
    }

    @Override
    public int hashCode() {
        return this.mark.hashCode();
    }

    public static boolean isCorrectMark(String mark) {
        return MARK_PATTERN.matcher(mark).matches();
    }
}
