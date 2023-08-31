package com.rposcro.jwavez.core.commands.controlled.builders.switchcolor;

public class SwitchColorCommandBuilder {

    private SwitchColorCommandBuilderV1 builderV1 = new SwitchColorCommandBuilderV1();

    public SwitchColorCommandBuilderV1 v1() {
        return this.builderV1;
    }
}
