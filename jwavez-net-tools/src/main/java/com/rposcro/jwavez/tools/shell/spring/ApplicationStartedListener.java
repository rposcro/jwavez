package com.rposcro.jwavez.tools.shell.spring;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.DongleInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    private final static String JWAVEZ_DEVICE_ENV = "JWAVEZ_DEVICE";

    @Autowired
    JWaveZShellContext shellContext;

    @Autowired
    private DongleInformationService dongleInformationService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            log.info("Application started event caught ...");
            String devicePath = determineDevice();
            DongleInformation dongleInformation = null;
            if (devicePath != null) {
                log.info("Collecting dongle information from " + devicePath);
                System.out.println("Collecting dongle information from " + devicePath);
                shellContext.setDongleDevicePath(devicePath);
                shellContext.setDongleInformation(dongleInformationService.collectDongleInformation());
            }
        } catch(SerialException e) {
            log.error("Failed to initialize detected dongle device!", e);
        }
    }

    private String determineDevice() {
        String device = System.getenv(JWAVEZ_DEVICE_ENV);
        if (device == null) {
            if (new File("/dev/cu.usbmodem21201").exists()) {
                device = "/dev/cu.usbmodem21201";
            } else {
                System.out.println("Note! No device detected!");
            }
        }
        return device;
    }
}
