package com.rposcro.jwavez.core.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

public enum SpecificDeviceClass {

    SPECIFIC_TYPE_NOT_USED(0x00, GenericDeviceClass.GENERIC_TYPE_NOT_KNOWN),

    SPECIFIC_TYPE_PORTABLE_REMOTE_CONTROLLER(0x01, GenericDeviceClass.GENERIC_TYPE_REMOTE_CONTROLLER),
    SPECIFIC_TYPE_PORTABLE_SCENE_CONTROLLER(0x02, GenericDeviceClass.GENERIC_TYPE_REMOTE_CONTROLLER),
    SPECIFIC_TYPE_PORTABLE_INSTALLER_TOOL(0x03, GenericDeviceClass.GENERIC_TYPE_REMOTE_CONTROLLER),

    SPECIFIC_TYPE_PC_CONTROLLER(0x01, GenericDeviceClass.GENERIC_TYPE_STATIC_CONTOLLER),
    SPECIFIC_TYPE_SCENE_CONTROLLER(0x02, GenericDeviceClass.GENERIC_TYPE_STATIC_CONTOLLER),
    SPECIFIC_TYPE_INSTALLER_TOOL(0x03, GenericDeviceClass.GENERIC_TYPE_STATIC_CONTOLLER),

    SPECIFIC_TYPE_SATELLITE_RECEIVER(0x04, GenericDeviceClass.GENERIC_TYPE_AV_CONTROL_POINT),
    SPECIFIC_TYPE_SATELLITE_RECEIVER_V2(0x11, GenericDeviceClass.GENERIC_TYPE_AV_CONTROL_POINT),
    SPECIFIC_TYPE_DOORBELL(0x12, GenericDeviceClass.GENERIC_TYPE_AV_CONTROL_POINT),

    SPECIFIC_TYPE_SIMPLE_DISPLAY(0x01, GenericDeviceClass.GENERIC_TYPE_DISPLAY),

    SPECIFIC_TYPE_THERMOSTAT_HEATING(0x01, GenericDeviceClass.GENERIC_TYPE_THERMOSTAT),
    SPECIFIC_TYPE_THERMOSTAT_GENERAL(0x02, GenericDeviceClass.GENERIC_TYPE_THERMOSTAT),
    SPECIFIC_TYPE_SETBACK_SCHEDULE_THERMOSTAT(0x03, GenericDeviceClass.GENERIC_TYPE_THERMOSTAT),
    SPECIFIC_TYPE_SETPOINT_THERMOSTAT(0x04, GenericDeviceClass.GENERIC_TYPE_THERMOSTAT),
    SPECIFIC_TYPE_SETBACK_THERMOSTAT(0x05, GenericDeviceClass.GENERIC_TYPE_THERMOSTAT),
    SPECIFIC_TYPE_THERMOSTAT_GENERAL_V2(0x06, GenericDeviceClass.GENERIC_TYPE_THERMOSTAT),

    SPECIFIC_TYPE_SIMPLE_WINDOW_COVERING(0x01, GenericDeviceClass.GENERIC_TYPE_WINDOW_COVERING),

    SPECIFIC_TYPE_BASIC_REPEATER_SLAVE(0x01, GenericDeviceClass.GENERIC_TYPE_REPEATER_SLAVE),

    SPECIFIC_TYPE_POWER_SWITCH_BINARY(0x01, GenericDeviceClass.GENERIC_TYPE_BINARY_SWITCH),
    SPECIFIC_TYPE_SCENE_SWITCH_BINARY_DISCONTINUED(0x02, GenericDeviceClass.GENERIC_TYPE_BINARY_SWITCH),
    SPECIFIC_TYPE_SCENE_SWITCH_BINARY(0x03, GenericDeviceClass.GENERIC_TYPE_BINARY_SWITCH),

    SPECIFIC_TYPE_POWER_SWITCH_MULTILEVEL(0x01, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),
    SPECIFIC_TYPE_SCENE_SWITCH_MULTILEVEL_DISCONTINUED(0x02, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),
    SPECIFIC_TYPE_MOTOR_MULTIPOSITION(0x03, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),
    SPECIFIC_TYPE_SCENE_SWITCH_MULTILEVEL(0x04, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),
    SPECIFIC_TYPE_MOTOR_CONTROL_CLASS_A(0x05, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),
    SPECIFIC_TYPE_MOTOR_CONTROL_CLASS_B(0x06, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),
    SPECIFIC_TYPE_MOTOR_CONTROL_CLASS_C(0x07, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SWITCH),

    SPECIFIC_TYPE_SWITCH_REMOTE_BINARY(0x01, GenericDeviceClass.GENERIC_TYPE_REMOTE_SWITCH),
    SPECIFIC_TYPE_SWITCH_REMOTE_MULTILEVEL(0x02, GenericDeviceClass.GENERIC_TYPE_REMOTE_SWITCH),
    SPECIFIC_TYPE_SWITCH_REMOTE_TOGGLE_BINARY(0x03, GenericDeviceClass.GENERIC_TYPE_REMOTE_SWITCH),
    SPECIFIC_TYPE_SWITCH_REMOTE_TOGGLE_MULTILEVEL(0x04, GenericDeviceClass.GENERIC_TYPE_REMOTE_SWITCH),

    SPECIFIC_TYPE_SWITCH_TOGGLE_BINARY(0x01, GenericDeviceClass.GENERIC_TYPE_TOGGLE_SWITCH),
    SPECIFIC_TYPE_SWITCH_TOGGLE_MULTILEVEL(0x02, GenericDeviceClass.GENERIC_TYPE_TOGGLE_SWITCH),

    SPECIFIC_TYPE_Z_IP_TUNNELING_GATEWAY(0x01, GenericDeviceClass.GENERIC_TYPE_Z_IP_GATEWAY),
    SPECIFIC_TYPE_Z_IP_ADVANCED_GATEWAY(0x02, GenericDeviceClass.GENERIC_TYPE_Z_IP_GATEWAY),

    SPECIFIC_TYPE_Z_IP_TUNNELING_NODE(0x01, GenericDeviceClass.GENERIC_TYPE_Z_IP_NODE),
    SPECIFIC_TYPE_Z_IP_ADVANCED_NODE(0x02, GenericDeviceClass.GENERIC_TYPE_Z_IP_NODE),

    SPECIFIC_TYPE_RESIDENTIAL_HEAT_RECOVERY_VENTILATION(0x01, GenericDeviceClass.GENERIC_TYPE_VENTILATION),

    SPECIFIC_TYPE_ROUTING_SENSOR_BINARY(0x01, GenericDeviceClass.GENERIC_TYPE_SENSOR_BINARY),

    SPECIFIC_TYPE_ROUTING_SENSOR_MULTILEVEL(0x01, GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SENSOR),

    SPECIFIC_TYPE_SIMPLE_METER(0x01, GenericDeviceClass.GENERIC_TYPE_METER),

    SPECIFIC_TYPE_DOOR_LOCK(0x01, GenericDeviceClass.GENERIC_TYPE_ENTRY_CONTROL),
    SPECIFIC_TYPE_ADVANCED_DOOR_LOCK(0x02, GenericDeviceClass.GENERIC_TYPE_ENTRY_CONTROL),
    SPECIFIC_TYPE_SECURE_KEYPAD_DOOR_LOCK(0x03, GenericDeviceClass.GENERIC_TYPE_ENTRY_CONTROL),

    SPECIFIC_TYPE_ENERGY_PRODUCTION(0x01, GenericDeviceClass.GENERIC_TYPE_SEMI_INTEROPERABLE),

    SPECIFIC_TYPE_ALARM_SENSOR_ROUTING_BASIC(0x01, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_ALARM_SENSOR_ROUTING(0x02, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_ALARM_SENSOR_ZENSOR_BASIC(0x03, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_ALARM_SENSOR_ZENSOR(0x04, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_ALARM_SENSOR_ZENSOR_ADVANCED(0x05, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_SMOKE_SENSOR_ROUTING_BASIC(0x06, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_SMOKE_SENSOR_ROUTING(0x07, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_SMOKE_SENSOR_ZENSOR_BASIC(0x08, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_SMOKE_SENSOR_ZENSOR(0x09, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),
    SPECIFIC_TYPE_SMOKE_SENSOR_ZENSOR_ADVANCED(0x0A, GenericDeviceClass.GENERIC_TYPE_ALARM_SENSOR),

    SPECIFIC_TYPE_NOTIFICATION_SENSOR(0x01, GenericDeviceClass.GENERIC_TYPE_SENSOR_NOTIFICATION);

    @Getter
    private GenericDeviceClass genericDeviceClass;
    @Getter
    private byte code;

    private static Map<Integer, SpecificDeviceClass> codeToClassMap;

    SpecificDeviceClass(int code, GenericDeviceClass genericDeviceClass) {
        this.code = (byte) code;
        this.genericDeviceClass = genericDeviceClass;
        registerConstant(this, code, genericDeviceClass);
    }

    private static void registerConstant(SpecificDeviceClass specificDeviceClass, int code, GenericDeviceClass genericDeviceClass) {
        if (codeToClassMap == null) {
            codeToClassMap = new HashMap<>();
        }
        codeToClassMap.put(key((byte) code, genericDeviceClass), specificDeviceClass);
    }

    public static SpecificDeviceClass ofCode(byte code, GenericDeviceClass genericDeviceClass) {
        return Optional.ofNullable(codeToClassMap.get(key(code, genericDeviceClass)))
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code + " of " + genericDeviceClass));
    }

    public static Optional<SpecificDeviceClass> ofCodeOptional(byte code, GenericDeviceClass genericDeviceClass) {
        return Optional.ofNullable(codeToClassMap.get(key(code, genericDeviceClass)));
    }

    private static int key(byte code, GenericDeviceClass genericDeviceClass) {
        return ((genericDeviceClass.getCode() & 0xFF) << 8) | (code & 0xFF);
    }
}
