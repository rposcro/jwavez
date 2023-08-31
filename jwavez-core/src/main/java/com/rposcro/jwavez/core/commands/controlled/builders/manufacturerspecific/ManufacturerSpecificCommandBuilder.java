package com.rposcro.jwavez.core.commands.controlled.builders.manufacturerspecific;

public class ManufacturerSpecificCommandBuilder {

    private ManufacturerSpecificCommandBuilderV1 builderV1 = new ManufacturerSpecificCommandBuilderV1();

    public ManufacturerSpecificCommandBuilderV1 v1() {
        return this.builderV1;
    }
}
