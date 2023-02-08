package com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel;

public class SwitchMultiLevelCommandBuilder {

    private SwitchMultiLevelCommandBuilderV1 builderV1 = new SwitchMultiLevelCommandBuilderV1();
    private SwitchMultiLevelCommandBuilderV2 builderV2 = new SwitchMultiLevelCommandBuilderV2();

    public SwitchMultiLevelCommandBuilderV1 v1() {
        return this.builderV1;
    }
    public SwitchMultiLevelCommandBuilderV2 v2() {
        return this.builderV2;
    }
}
