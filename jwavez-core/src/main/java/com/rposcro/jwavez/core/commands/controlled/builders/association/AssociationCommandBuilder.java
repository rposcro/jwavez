package com.rposcro.jwavez.core.commands.controlled.builders.association;

public class AssociationCommandBuilder {

    private AssociationCommandBuilderV1 builderV1 = new AssociationCommandBuilderV1();
    private AssociationCommandBuilderV2 builderV2 = new AssociationCommandBuilderV2();

    public AssociationCommandBuilderV1 v1() {
        return this.builderV1;
    }

    public AssociationCommandBuilderV2 v2() {
        return this.builderV2;
    }
}
