package com.rposcro.jwavez.tools.shell.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeInformation {

    private int nodeId;
    private String nodeMemo;

    private NodeProductInformation productInformation;
    private NodeParametersInformation parametersInformation;

    public NodeInformation() {
        this.parametersInformation = new NodeParametersInformation();
    }
}
