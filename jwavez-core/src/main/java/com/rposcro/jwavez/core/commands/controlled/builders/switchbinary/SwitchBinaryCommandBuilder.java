package com.rposcro.jwavez.core.commands.controlled.builders.switchbinary;

public class SwitchBinaryCommandBuilder {

    private SwitchBinaryCommandBuilderV1 builderV1 = new SwitchBinaryCommandBuilderV1();
    private SwitchBinaryCommandBuilderV2 builderV2 = new SwitchBinaryCommandBuilderV2();

    public SwitchBinaryCommandBuilderV1 v1() {
        return this.builderV1;
    }

    public SwitchBinaryCommandBuilderV2 v2() {
        return this.builderV2;
    }
}
