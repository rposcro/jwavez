package com.rposcro.jwavez.tools.shell.commands.generic;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellOption;

import java.io.File;

@ShellComponent
@Command(group = CommandGroup.GENERIC)
public class ContextCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private DongleInformationService dongleInformationService;

    @Autowired
    private RepositoryService repositoryService;

    @Command(command = "pwc", description = "Print current context information")
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

    @Command(command = "about", description = "Print information about this application")
    public String about() {
        return "JWaveZ Network Shell";
    }

    @Command(command = "device", description = "Set current device")
    public String setCurrentDevice(@ShellOption(value = {"--path-to-device", "-path"}) String pathToDevice
    ) throws SerialException {
        File deviceFile = new File(pathToDevice);
        if (!deviceFile.exists()) {
            return "Incorrect device file! Current device not changed";
        }

        shellContext.setDongleDevicePath(pathToDevice);
        DongleInformation dongleInformation = dongleInformationService.collectDongleInformation();
        shellContext.setDongleInformation(dongleInformation);

        String message = "Current device changed to " + pathToDevice;
        if (shellContext.isRepositoryOpened()) {
            message += ", repository detached";
        }

        repositoryService.detachRepository();
        return message;
    }
}
