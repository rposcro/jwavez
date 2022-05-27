package com.rposcro.jwavez.core.classes;

import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;
import lombok.Getter;

import java.util.Optional;

public enum CommandClass implements EncodableConstant {

  CMD_CLASS_NO_OPERATION(0x00),
  CMD_CLASS_BASIC(0x20),
  CMD_CLASS_CONTROLLER_REPLICATION(0x21),
  CMD_CLASS_APPLICATION_STATUS(0x22),
  CMD_CLASS_ZIP(0x23),
  CMD_CLASS_SWITCH_BINARY(0x25),
  CMD_CLASS_SWITCH_MULTILEVEL(0x26),
  CMD_CLASS_SWITCH_ALL(0x27),
  CMD_CLASS_SWITCH_TOGGLE_BINARY(0x28),
  CMD_CLASS_SWITCH_TOGGLE_MULTILEVEL(0x29),
  CMD_CLASS_CHIMNEY_FAN(0x2A),
  CMD_CLASS_SCENE_ACTIVATION(0x2B),
  CMD_CLASS_SCENE_ACTUATOR_CONF(0x2C),
  CMD_CLASS_SCENE_CONTROLLER_CONF(0x2D),
  CMD_CLASS_SENSOR_BINARY(0x30),
  CMD_CLASS_SENSOR_MULTILEVEL(0x31),
  CMD_CLASS_METER(0x32),
  CMD_CLASS_SWITCH_COLOR(0x33),
  CMD_CLASS_NETWORK_MANAGEMENT_INCLUSION(0x34),
  CMD_CLASS_METER_PULSE(0x35),
  CMD_CLASS_METER_TBL_CONFIG(0x3C),
  CMD_CLASS_METER_TBL_MONITOR(0x3D),
  CMD_CLASS_METER_TBL_PUSH(0x3E),
  CMD_CLASS_THERMOSTAT_HEATING(0x38),
  CMD_CLASS_THERMOSTAT_MODE(0x40),
  CMD_CLASS_THERMOSTAT_OPERATING_STATE(0x42),
  CMD_CLASS_THERMOSTAT_SETPOINT(0x43),
  CMD_CLASS_THERMOSTAT_FAN_MODE(0x44),
  CMD_CLASS_THERMOSTAT_FAN_STATE(0x45),
  CMD_CLASS_CLIMATE_CONTROL_SCHEDULE(0x46),
  CMD_CLASS_THERMOSTAT_SETBACK(0x47),
  CMD_CLASS_DOOR_LOCK_LOGGING(0x4C),
  CMD_CLASS_SCHEDULE_ENTRY_LOCK(0x4E),
  CMD_CLASS_BASIC_WINDOW_COVERING(0x50),
  CMD_CLASS_MTP_WINDOW_COVERING(0x51),
  CMD_CLASS_ASSOCIATION_GRP_INFO(0x59),
  CMD_CLASS_CENTRAL_SCENE(0x5B),
  CMD_CLASS_ZWAVE_PLUS_INFO(0x5E),
  CMD_CLASS_MULTI_CHANNEL(0x60),
  CMD_CLASS_DOOR_LOCK(0x62),
  CMD_CLASS_USER_CODE(0x63),
  CMD_CLASS_CONFIGURATION(0x70),
  CMD_CLASS_ALARM(0x71),
  CMD_CLASS_MANUFACTURER_SPECIFIC(0x72),
  CMD_CLASS_POWERLEVEL(0x73),
  CMD_CLASS_PROTECTION(0x75),
  CMD_CLASS_LOCK(0x76),
  CMD_CLASS_NODE_NAMING(0x77),
  CMD_CLASS_FIRMWARE_UPDATE_MD(0x7A),
  CMD_CLASS_GROUPING_NAME(0x7B),
  CMD_CLASS_REMOTE_ASSOCIATION_ACTIVATE(0x7C),
  CMD_CLASS_REMOTE_ASSOCIATION(0x7D),
  CMD_CLASS_BATTERY(0x80),
  CMD_CLASS_CLOCK(0x81),
  CMD_CLASS_HAIL(0x82),
  CMD_CLASS_WAKE_UP(0x84),
  CMD_CLASS_ASSOCIATION(0x85),
  CMD_CLASS_VERSION(0x86),
  CMD_CLASS_INDICATOR(0x87),
  CMD_CLASS_PROPRIETARY(0x88),
  CMD_CLASS_LANGUAGE(0x89),
  CMD_CLASS_TIME(0x8A),
  CMD_CLASS_TIME_PARAMETERS(0x8B),
  CMD_CLASS_GEOGRAPHIC_LOCATION(0x8C),
  CMD_CLASS_COMPOSITE(0x8D),
  CMD_CLASS_MULTI_CHANNEL_ASSOCIATION(0x8E),
  CMD_CLASS_MULTI_CMD(0x8F),
  CMD_CLASS_ENERGY_PRODUCTION(0x90),
  CMD_CLASS_MANUFACTURER_PROPRIETARY(0x91),
  CMD_CLASS_SCREEN_MD(0x92),
  CMD_CLASS_SCREEN_ATTRIBUTES(0x93),
  CMD_CLASS_SIMPLE_AV_CONTROL(0x94),
  CMD_CLASS_AV_CONTENT_DIRECTORY_MD(0x95),
  CMD_CLASS_AV_RENDERER_STATUS(0x96),
  CMD_CLASS_AV_CONTENT_SEARCH_MD(0x97),
  CMD_CLASS_SECURITY(0x98),
  CMD_CLASS_AV_TAGGING_MD(0x99),
  CMD_CLASS_IP_CONFIGURATION(0x9A),
  CMD_CLASS_ASSOCIATION_COMMAND_CONFIGURATION(0x9B),
  CMD_CLASS_SENSOR_ALARM(0x9C),
  CMD_CLASS_SILENCE_ALARM(0x9D),
  CMD_CLASS_SENSOR_CONFIGURATION(0x9E),
  CMD_CLASS_MARK(0xEF, true),
  CMD_CLASS_NON_INTEROPERABLE(0xF0),
  CMD_CLASS_UNKNOWN(-1),
    ;

  @Getter
  private boolean marker;

  CommandClass(int code) {
    this(code, false);
  }

  CommandClass(int code, boolean marker) {
    this.marker = marker;
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static CommandClass ofCode(byte code) {
    return EncodableConstantsRegistry.optionalConstantOfCode(CommandClass.class, code)
        .orElseThrow(() -> new CommandNotSupportedException("CommandClass of code " + code + " unknown!"));
  }

  public static Optional<CommandClass> optionalOfCode(byte code) {
    return EncodableConstantsRegistry.optionalConstantOfCode(CommandClass.class, code);
  }
}
