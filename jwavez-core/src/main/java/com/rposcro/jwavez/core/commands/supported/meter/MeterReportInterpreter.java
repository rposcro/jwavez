package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.model.MeterType;
import com.rposcro.jwavez.core.model.MeterUnit;

public class MeterReportInterpreter {

    private MeterReport meterReport;

    MeterReportInterpreter(MeterReport meterReport) {
        this.meterReport = meterReport;
    }

    public MeterType decodedMeterType() {
        return MeterType.ofCodeOptional(meterReport.getMeterType()).orElse(null);
    }

    public MeterUnit decodedMeterUnit() {
        return recognizeMeterUnit(decodedMeterType());
    }

    private MeterUnit recognizeMeterUnit(MeterType meterType) {
        if (meterType == null) {
            return null;
        }

        switch (meterType) {
            case ELECTRIC_METER:
                return recognizeMeterUnitForElectricMeter(meterReport);
            case GAS_METER:
                return recognizeMeterUnitForGasMeter(meterReport);
            case WATER_METER:
                return recognizeMeterUnitForWaterMeter(meterReport);
            case HEATING_METER:
                return recognizeMeterUnitForHeatingMeter(meterReport);
            case COOLING_METER:
                return recognizeMeterUnitForCoolingMeter(meterReport);
            default:
                return null;
        }
    }

    private MeterUnit recognizeMeterUnitForElectricMeter(MeterReport report) {
        switch ((report.getScale2() << 3) | report.getScale()) {
            case 0:
                return MeterUnit.kWh;
            case 1:
                return MeterUnit.kVAh;
            case 2:
                return MeterUnit.W;
            case 3:
                return MeterUnit.PulseCount;
            case 4:
                return MeterUnit.V;
            case 5:
                return MeterUnit.A;
            case 6:
                return MeterUnit.PowerFactor;
            case 7 + 0:
                return MeterUnit.kVar;
            case 7 + 8:
                return MeterUnit.kVarh;
            default:
                return null;
        }
    }

    private MeterUnit recognizeMeterUnitForGasMeter(MeterReport meterReport) {
        switch (meterReport.getScale()) {
            case 0:
                return MeterUnit.CubicMeters;
            case 1:
                return MeterUnit.CubicFeet;
            case 3:
                return MeterUnit.PulseCount;
            default:
                return null;
        }
    }

    private MeterUnit recognizeMeterUnitForWaterMeter(MeterReport meterReport) {
        switch (meterReport.getScale()) {
            case 0:
                return MeterUnit.CubicMeters;
            case 1:
                return MeterUnit.CubicFeet;
            case 2:
                return MeterUnit.UsGallons;
            case 3:
                return MeterUnit.PulseCount;
            default:
                return null;
        }
    }

    private MeterUnit recognizeMeterUnitForHeatingMeter(MeterReport meterReport) {
        switch (meterReport.getScale()) {
            case 0:
                return MeterUnit.kWh;
            default:
                return null;
        }
    }

    private MeterUnit recognizeMeterUnitForCoolingMeter(MeterReport meterReport) {
        switch (meterReport.getScale()) {
            case 0:
                return MeterUnit.kWh;
            default:
                return null;
        }
    }
}
