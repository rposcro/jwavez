package com.rposcro.jwavez.core.commands.controlled.builders.configuration;

public class ConfigurationCommandBuilder {

    private ConfigurationCommandBuilderV1 builderV1 = new ConfigurationCommandBuilderV1();
    private ConfigurationCommandBuilderV2 builderV2 = new ConfigurationCommandBuilderV2();
    private ConfigurationCommandBuilderV3 builderV3 = new ConfigurationCommandBuilderV3();

    public ConfigurationCommandBuilderV1 v1() {
        return this.builderV1;
    }

    public ConfigurationCommandBuilderV2 v2() {
        return this.builderV2;
    }
    public ConfigurationCommandBuilderV3 v3() {
        return this.builderV3;
    }
}
