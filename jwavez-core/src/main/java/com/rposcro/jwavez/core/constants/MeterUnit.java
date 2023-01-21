package com.rposcro.jwavez.core.constants;

import lombok.Getter;

@Getter
public enum MeterUnit {

    kWh("Kilo Watt Hour"),
    kVAh("Kilo Volt Ampere Hours"),
    W("Watt"),
    PulseCount("Pulse Count"),
    V("Volt"),
    A("Ampere"),
    PowerFactor("Power Factor"),
    kVar("Kilo Reactive Power"),
    kVarh("Kilo Reactive Power Hours"),
    CubicMeters("Cubic Meters"),
    CubicFeet("Cubic Feet"),
    UsGallons("US Gallons"),
    Unspecified("Unspecified")
    ;

    private String unitName;

    MeterUnit(String unitName) {
        this.unitName = unitName;
    }
}
