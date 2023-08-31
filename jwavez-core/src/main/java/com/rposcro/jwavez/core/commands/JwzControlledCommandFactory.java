package com.rposcro.jwavez.core.commands;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.basic.BasicCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.manufacturerspecific.ManufacturerSpecificCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannelassociation.MultiChannelAssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.sensorbinary.SensorBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.version.VersionCommandBuilder;

import java.util.HashMap;
import java.util.Map;

public class JwzControlledCommandFactory {

    private final Map<CommandClass, Object> builders = new HashMap<>();

    public AssociationCommandBuilder associationCommandBuilder() {
        return (AssociationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_ASSOCIATION, cmdClass -> new AssociationCommandBuilder());
    }

    public BasicCommandBuilder basicCommandBuilder() {
        return (BasicCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_BASIC, cmdClass -> new BasicCommandBuilder());
    }

    public ConfigurationCommandBuilder configurationCommandBuilder() {
        return (ConfigurationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_CONFIGURATION, cmdClass -> new ConfigurationCommandBuilder());
    }

    public ManufacturerSpecificCommandBuilder manufacturerSpecificCommandBuilder() {
        return (ManufacturerSpecificCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_MANUFACTURER_SPECIFIC, cmdClass -> new ManufacturerSpecificCommandBuilder());
    }

    public MultiChannelCommandBuilder multiChannelCommandBuilder() {
        return (MultiChannelCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_MULTI_CHANNEL, cmdClass -> new MultiChannelCommandBuilder());
    }

    public MultiChannelAssociationCommandBuilder multiChannelAssociationCommandBuilder() {
        return (MultiChannelAssociationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, cmdClass -> new MultiChannelAssociationCommandBuilder());
    }

    public PowerLevelCommandBuilder powerLevelCommandBuilder() {
        return (PowerLevelCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_POWERLEVEL, cmdClass -> new PowerLevelCommandBuilder());
    }

    public SensorBinaryCommandBuilder sensorBinaryCommandBuilder() {
        return (SensorBinaryCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_SENSOR_BINARY, cmdClass -> new SensorBinaryCommandBuilder());
    }

    public SwitchBinaryCommandBuilder switchBinaryCommandBuilder() {
        return (SwitchBinaryCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_SWITCH_BINARY, cmdClass -> new SwitchBinaryCommandBuilder());
    }

    public SwitchColorCommandBuilder switchColorCommandBuilder() {
        return (SwitchColorCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_SWITCH_COLOR, cmdClass -> new SwitchColorCommandBuilder());
    }

    public SwitchMultiLevelCommandBuilder switchMultiLevelCommandBuilder() {
        return (SwitchMultiLevelCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_SWITCH_MULTILEVEL, cmdClass -> new SwitchMultiLevelCommandBuilder());
    }

    public VersionCommandBuilder versionCommandBuilder() {
        return (VersionCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_VERSION, cmdClass -> new VersionCommandBuilder());
    }
}
