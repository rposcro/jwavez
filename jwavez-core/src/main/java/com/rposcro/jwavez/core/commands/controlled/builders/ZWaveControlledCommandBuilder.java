package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.builders.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.SensorBinaryCommandBuilder;

import java.util.Map;

public class ZWaveControlledCommandBuilder {

  private Map<CommandClass, Object> builders;

  public AssociationCommandBuilder associationCommandBuilder() {
    return (AssociationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_ASSOCIATION, cmdClass -> new AssociationCommandBuilder());
  }

  public ConfigurationCommandBuilder configurationCommandBuilder() {
    return (ConfigurationCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_CONFIGURATION, cmdClass -> new ConfigurationCommandBuilder());
  }

  public MultiChannelCommandBuilder multiChannelCommandBuilder() {
    return (MultiChannelCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_MULTI_CHANNEL, cmdClass -> new MultiChannelCommandBuilder());
  }

  public SensorBinaryCommandBuilder sensorBinaryControlledCommand() {
    return (SensorBinaryCommandBuilder) builders.computeIfAbsent(CommandClass.CMD_CLASS_SENSOR_BINARY, cmdClass -> new SensorBinaryCommandBuilder());
  }
}
