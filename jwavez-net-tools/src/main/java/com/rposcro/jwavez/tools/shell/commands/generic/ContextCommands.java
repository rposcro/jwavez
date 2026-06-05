package com.rposcro.jwavez.tools.shell.commands.generic;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.dongle.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import com.rposcro.jwavez.tools.utils.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
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
    public String setCurrentDevice(@Argument(index = 0, description = "Path to dongle device to open communication with") String pathToDevice
    ) throws SerialException {
        if (Strings.isNullOrEmpty(pathToDevice)) {
            if (shellContext.getDongleDevicePath() == null) {
                return "No dongle device is active";
            } else {
                return "Current dongle device is %s".formatted(shellContext.getDongleDevicePath());
            }
        }

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
