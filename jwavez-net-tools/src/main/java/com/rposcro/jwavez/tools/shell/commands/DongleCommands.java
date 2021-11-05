package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.ShellContext;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.DongleCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup("Dongle")
public class DongleCommands {

    @Autowired
    private ShellContext shellContext;

    @Autowired
    private DongleCheckService dongleCheckService;

    @Autowired
    private Shell shell;

    @ShellMethod(value = "Shows current dongle information", key="info")
    public String showInfo() throws SerialException {
        DongleInformation dongleInformation = dongleCheckService.collectDongleInformation();
        return String.format("** Network Information\n%s\n\n"
                + "** Dongle Information\n%s\n\n"
                + "** Device Information\n%s\n\n"
                + "** Functions\n%s\n"
                , dongleInformation.formatNetworkInfo()
                , dongleInformation.formatDongleInfo()
                , dongleInformation.formatDeviceInfo()
                , dongleInformation.formatFunctionsInfo()
        );
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (!"dongle".equals(shellContext.getWorkingScope().getName())) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDevice() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }
}
