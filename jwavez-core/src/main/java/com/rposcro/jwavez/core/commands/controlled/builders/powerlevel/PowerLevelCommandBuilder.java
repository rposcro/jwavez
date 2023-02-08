package com.rposcro.jwavez.core.commands.controlled.builders.powerlevel;

public class PowerLevelCommandBuilder {

    private PowerLevelCommandBuilderV1 builderV1 = new PowerLevelCommandBuilderV1();

    public PowerLevelCommandBuilderV1 v1() {
        return this.builderV1;
    }
}
