package com.rposcro.jwavez.core.commands.controlled.builders.switchcolor;

public class SwitchColorCommandBuilder {

    private SwitchColorCommandBuilderV1 builderV1 = new SwitchColorCommandBuilderV1();
    private SwitchColorCommandBuilderV2 builderV2 = new SwitchColorCommandBuilderV2();

    public SwitchColorCommandBuilderV1 v1() {
        return this.builderV1;
    }
    public SwitchColorCommandBuilderV2 v2() {
        return this.builderV2;
    }
}
