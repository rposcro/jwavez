package com.rposcro.jwavez.core.commands.controlled.builders.basic;

public class BasicCommandBuilder {

    private BasicCommandBuilderV1 builderV1 = new BasicCommandBuilderV1();

    public BasicCommandBuilderV1 v1() {
        return this.builderV1;
    }
}
