package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.model.MeterType;
import com.rposcro.jwavez.core.model.MeterUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeterReportInterpreterTest {

    @Mock
    private MeterReport report;

    @ParameterizedTest
    @EnumSource(MeterType.class)
    public void testDecodeMeterType(MeterType meterType) {
        when(report.getMeterType()).thenReturn(meterType.getCode());

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertEquals(meterType, interpreter.decodedMeterType());
    }

    @Test
    public void testDecodeMeterTypeNull() {
        when(report.getMeterType()).thenReturn((byte) 0x40);

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertNull(interpreter.decodedMeterType());
    }

    @Test
    public void testElectricMeterPulseCount() {
        when(report.getMeterType()).thenReturn(MeterType.ELECTRIC_METER.getCode());
        when(report.getScale()).thenReturn((byte) 0x03);
        when(report.getScale2()).thenReturn((short) 0x00);

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertEquals(MeterUnit.PulseCount, interpreter.decodedMeterUnit());
    }

    @Test
    public void testElectricMeterkVar() {
        when(report.getMeterType()).thenReturn(MeterType.ELECTRIC_METER.getCode());
        when(report.getScale()).thenReturn((byte) 0x07);
        when(report.getScale2()).thenReturn((short) 0x00);

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertEquals(MeterUnit.kVar, interpreter.decodedMeterUnit());
    }

    @Test
    public void testElectricMeterkVarh() {
        when(report.getMeterType()).thenReturn(MeterType.ELECTRIC_METER.getCode());
        when(report.getScale()).thenReturn((byte) 0x07);
        when(report.getScale2()).thenReturn((short) 0x01);

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertEquals(MeterUnit.kVarh, interpreter.decodedMeterUnit());
    }

    @Test
    public void testHeatingMeterkWh() {
        when(report.getMeterType()).thenReturn(MeterType.HEATING_METER.getCode());
        when(report.getScale()).thenReturn((byte) 0x00);

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertEquals(MeterUnit.kWh, interpreter.decodedMeterUnit());
    }

    @Test
    public void testCoolingMeterNull() {
        when(report.getMeterType()).thenReturn(MeterType.COOLING_METER.getCode());
        when(report.getScale()).thenReturn((byte) 0x01);

        MeterReportInterpreter interpreter = new MeterReportInterpreter(report);

        assertNull(interpreter.decodedMeterUnit());
    }
}
