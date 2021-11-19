package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeParametersInformation {

    private final List<ParameterMeta> parameterMetas;
    private final Map<Integer, Integer> parameterValues;

    public NodeParametersInformation() {
        this.parameterMetas = new ArrayList<>();
        this.parameterValues = new HashMap<>();
    }

    @JsonCreator
    public NodeParametersInformation(
            @JsonProperty("parameterMetas") List<ParameterMeta> parameterMetas,
            @JsonProperty("parameterValues") Map<Integer, Integer> parameterValues) {
        this();
        if (parameterMetas != null) {
            this.parameterMetas.addAll(parameterMetas);
        }
        if (parameterValues != null) {
            this.parameterValues.putAll(parameterValues);
        }
    }

    public boolean isParameterDefined(int paramNumber) {
        return findParameterMeta(paramNumber) != null;
    }

    public List<ParameterMeta> getParameterMetas() {
        return Collections.unmodifiableList(parameterMetas);
    }

    public ParameterMeta findParameterMeta(int paramNumber) {
        ParameterMeta parameterMeta = parameterMetas.stream()
                .filter(meta -> meta.getNumber() == paramNumber)
                .findFirst()
                .orElse(null);
        return parameterMeta;
    }

    public ParameterMeta removeParameterMeta(int paramNumber) {
        ParameterMeta parameterMeta = findParameterMeta(paramNumber);
        parameterMetas.remove(parameterMeta);
        return parameterMeta;
    }

    public ParameterMeta addOrReplaceParameterMeta(ParameterMeta parameterMeta) {
        ParameterMeta existingParameterMeta = findParameterMeta(parameterMeta.getNumber());
        if (existingParameterMeta != null) {
            parameterMetas.remove(existingParameterMeta);
        }
        parameterMetas.add(parameterMeta);
        Collections.sort(parameterMetas, Comparator.comparingInt(ParameterMeta::getNumber));
        return existingParameterMeta;
    }

    public Integer findParameterValue(int paramNumber) {
        return parameterValues.get(paramNumber);
    }

    public Integer setParameterValue(int paramNumber, int paramValue) {
        return parameterValues.put(paramNumber, paramValue);
    }

    public Integer removeParameterValue(int paramNumber) {
        return parameterValues.remove(paramNumber);
    }

    public void wipeOutAll() {
        parameterMetas.clear();
        parameterValues.clear();
    }
}