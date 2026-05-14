package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import java.io.File;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.GENERIC)
public class ContextCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private DongleInformationService dongleInformationService;

    @Autowired
    private RepositoryService repositoryService;

    @Command(name = "pwc", description = "Print current context information")
    public String printContextInformation() {
        StringBuffer message = new StringBuffer();
        message.append("Current working scope is " + shellContext.getShellScope().getScopePath()).append("\n");
        message.append(shellContext.isDeviceReady() ?
                "Current device is " + shellContext.getDongleDevicePath() : "No device is ready").append("\n");
        message.append(shellContext.isRepositoryOpened() ?
                "Current repository is " + shellContext.getRepositoryName() : "No repository is opened").append("\n");
        message.append(shellContext.getScopeContext().formatContext());
        return message.toString();
    }

    @Command(name = "about")
    public String about() {
        return "JWaveZ Network Shell";
    }

    @Command(name = "device", description = "Set current device")
    public String setCurrentDevice(@Option(shortName = 'p', longName = "path-to-device", required = true) String pathToDevice
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
