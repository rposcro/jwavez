package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;

@ShellComponent
@ShellCommandGroup(CommandGroup.GENERIC)
public class ContextCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private DongleInformationService dongleInformationService;

    @Autowired
    private RepositoryService repositoryService;

    @ShellMethod(value = "Show context value {device, scope}", key="show")
    public String show(@ShellOption(value = { "--property-name", "-pname" }) String propertyName) {
        if ("device".equals(propertyName)) {
            if (shellContext.getDongleDevicePath() == null) {
                return "No current device is set";
            }
            return "Current device is " + shellContext.getDongleDevicePath();
        } else if ("scope".equals(propertyName)) {
            return "Current working scope is " + shellContext.getShellScope().getScopePath();
        }

        return "Don't know what '" + propertyName + "' is, chose from {device|scope}";
    }

    @ShellMethod(value = "About")
    public String about() {
        return "JWaveZ Network Shell";
    }

    @ShellMethod(value = "Set current device", key="device")
    public String setCurrentDevice(@ShellOption(value = { "--path-to-device", "-path" }) String pathToDevice
    ) throws SerialException {
        File deviceFile = new File(pathToDevice);
        if (!deviceFile.exists()) {
            return "Incorrect device file! Current device not changed";
        }

        DongleInformation dongleInformation = dongleInformationService.collectDongleInformation();
        shellContext.setDongleInformation(dongleInformation);
        shellContext.setDongleDevicePath(pathToDevice);

        String message = "Current device changed to " + pathToDevice;
        if (shellContext.isRepositoryOpened()) {
            message += ", repository detached";
        }

        repositoryService.detachRepository();
        return message;
    }
}
