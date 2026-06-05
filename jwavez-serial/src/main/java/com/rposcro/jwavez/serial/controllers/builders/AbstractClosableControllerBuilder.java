package com.rposcro.jwavez.serial.controllers.builders;

import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.port.JSerialComPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import lombok.Getter;

import static com.rposcro.jwavez.core.utils.AssertUtil.nonNull;

@Getter
public class AbstractClosableControllerBuilder<T extends AbstractClosableControllerBuilder> {

    private String dongleDevice;
    private SerialPort serialPort;
    private RxTxConfiguration rxTxConfiguration;

    public T dongleDevice(String dongleDevice) {
        this.dongleDevice = dongleDevice;
        return (T) this;
    }

    public T serialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
        return (T) this;
    }

    public T rxTxConfiguration(RxTxConfiguration rxTxConfiguration) {
        this.rxTxConfiguration = rxTxConfiguration;
        return (T) this;
    }

    protected void fillDefaults() {
        if (serialPort == null) {
            this.serialPort = new JSerialComPort();
        }
    }

    protected void assureReadiness() {
        nonNull(this.dongleDevice, "Dongle device cannot be null!");
        nonNull(this.rxTxConfiguration, "RxTxConfiguration cannot be null!");
    }
}
