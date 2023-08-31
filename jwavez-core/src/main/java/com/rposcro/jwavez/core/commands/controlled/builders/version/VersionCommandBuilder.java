package com.rposcro.jwavez.core.commands.controlled.builders.version;

public class VersionCommandBuilder {

    private VersionCommandBuilderV1 builderV1 = new VersionCommandBuilderV1();
    private VersionCommandBuilderV3 builderV3 = new VersionCommandBuilderV3();

    public VersionCommandBuilderV1 v1() {
        return this.builderV1;
    }

    public VersionCommandBuilderV3 v3() {
        return this.builderV3;
    }
}
