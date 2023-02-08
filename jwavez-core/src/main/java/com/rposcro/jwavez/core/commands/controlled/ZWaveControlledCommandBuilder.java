package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.builders.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.SensorBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.basic.BasicCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.manufacturerspecific.ManufacturerSpecificCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.version.VersionCommandBuilder;

import java.util.HashMap;
import java.util.Map;

public class ZWaveControlledCommandBuilder {

    private final static Map<CommandClass, Object> builders = new HashMap<>();

    public static AssociationCommandBuilder associationCommandBuilder() {
        return (AssociationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_ASSOCIATION, cmdClass -> new AssociationCommandBuilder());
    }

    public static BasicCommandBuilder basicCommandBuilder() {
        return (BasicCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_BASIC, cmdClass -> new BasicCommandBuilder());
    }

    public static ConfigurationCommandBuilder configurationCommandBuilder() {
        return (ConfigurationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_CONFIGURATION, cmdClass -> new ConfigurationCommandBuilder());
    }

    public static ManufacturerSpecificCommandBuilder manufacturerSpecificCommandBuilder() {
        return (ManufacturerSpecificCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_MANUFACTURER_SPECIFIC, cmdClass -> new ManufacturerSpecificCommandBuilder());
    }

    public static MultiChannelCommandBuilder multiChannelCommandBuilder() {
        return (MultiChannelCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_MULTI_CHANNEL, cmdClass -> new MultiChannelCommandBuilder());
    }

    public static PowerLevelCommandBuilder powerLevelCommandBuilder() {
        return (PowerLevelCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_POWERLEVEL, cmdClass -> new PowerLevelCommandBuilder());
    }

    public static SensorBinaryCommandBuilder sensorBinaryControlledCommand() {
        return (SensorBinaryCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_SENSOR_BINARY, cmdClass -> new SensorBinaryCommandBuilder());
    }

    public static VersionCommandBuilder versionCommandBuilder() {
        return (VersionCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_VERSION, cmdClass -> new VersionCommandBuilder());
    }
}
