package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceBasedOptions;

public abstract class AbstractSyncBasedCommand extends AbstractCommand {

    protected BasicSynchronousController controller;
    protected SerialRequestFactory serialRequestFactory = JwzSerialSupport.defaultSupport().serialRequestFactory();

    protected void connect(AbstractDeviceBasedOptions options) throws SerialException {
        controller = BasicSynchronousController.builder()
                .dongleDevice(options.getDevice())
                .build()
                .connect();
    }

    @Override
    public void close() throws SerialPortException {
        controller.close();
    }
}
