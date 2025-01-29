package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NodeParametersInformation {

    private final Map<Integer, Long> parameterValues;

    public NodeParametersInformation() {
        this.parameterValues = new HashMap<>();
    }

    @JsonCreator
    public NodeParametersInformation(
            @JsonProperty("parameterValues") Map<Integer, Long> parameterValues) {
        this();
        if (parameterValues != null) {
            this.parameterValues.putAll(parameterValues);
        }
    }

    public Map<Integer, Long> getParameterValues() {
        return Collections.unmodifiableMap(parameterValues);
    }

    public Long findParameterValue(int paramNumber) {
        return parameterValues.get(paramNumber);
    }

    public Long setParameterValue(int paramNumber, long paramValue) {
        return parameterValues.put(paramNumber, paramValue);
    }

    public Long removeParameterValue(int paramNumber) {
        return parameterValues.remove(paramNumber);
    }

    public void wipeOutAll() {
        parameterValues.clear();
    }
}
