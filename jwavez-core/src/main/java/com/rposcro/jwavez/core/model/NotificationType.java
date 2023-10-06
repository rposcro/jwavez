package com.rposcro.jwavez.core.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

import java.util.Optional;

public enum NotificationType implements EncodableConstant {

    SMOKE_ALARM(1),
    CO_ALARM(2),
    CO2_ALARM(3),
    HEAT_ALARM(4),
    WATER_ALARM(5),
    ACCESS_CONTROL(6),
    HOME_SECURITY(7),
    POWER_MANAGEMENT(8),
    SYSTEM(9),
    EMERGENCY_ALARM(0x10),
    CLOCK(11),
    APPLIANCE(12),
    HOME_HEALTH(13),
    SIREN(14),
    WATER_VALVE(15),
    WEATHER_ALARM(16),
    IRRIGATION(17),
    GAS_ALARM(18),
    PEST_CONTROL(19),
    LIGHT_SENSOR(20),
    WATER_QUALITY_MONITORING(21),
    HOME_MONITORING(22),
    REQUEST_PENDING_NOTIFICATION(0xff),
    ;

    NotificationType(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static NotificationType ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(NotificationType.class, code);
    }

    public static Optional<NotificationType> ofCodeOptional(byte code) {
        return EncodableConstantsRegistry.optionalConstantOfCode(NotificationType.class, code);
    }
}
