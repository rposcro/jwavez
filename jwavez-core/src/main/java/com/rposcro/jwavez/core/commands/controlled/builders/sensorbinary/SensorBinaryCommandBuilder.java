package com.rposcro.jwavez.core.commands.controlled.builders.sensorbinary;

public class SensorBinaryCommandBuilder {

    private SensorBinaryCommandBuilderV1 builderV1 = new SensorBinaryCommandBuilderV1();
    private SensorBinaryCommandBuilderV2 builderV2 = new SensorBinaryCommandBuilderV2();

    public SensorBinaryCommandBuilderV1 v1() {
        return this.builderV1;
    }

    public SensorBinaryCommandBuilderV2 v2() {
        return this.builderV2;
    }
}
