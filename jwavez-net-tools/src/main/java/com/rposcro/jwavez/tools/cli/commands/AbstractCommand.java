package com.rposcro.jwavez.tools.cli.commands;

public abstract class AbstractCommand implements Command {

    private static byte callbackFlowId;

    protected byte nextFlowId() {
        return ++callbackFlowId == 0 ? ++callbackFlowId : callbackFlowId;
    }
}
