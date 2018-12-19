package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.enums.CommandClass;
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

  public SensorBinaryControlledCommand sensorBinaryControlledCommand() {
    return (SensorBinaryControlledCommand) builders.computeIfAbsent(CommandClass.CMD_CLASS_SENSOR_BINARY, cmdClass -> new SensorBinaryControlledCommand());
  }
}
