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

    @ShellMethod(value = "Print current context information", key = "pwc")
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

    @ShellMethod(value = "About")
    public String about() {
        return "JWaveZ Network Shell";
    }

    @ShellMethod(value = "Set current device", key = "device")
    public String setCurrentDevice(@ShellOption(value = {"--path-to-device", "-path"}) String pathToDevice
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
