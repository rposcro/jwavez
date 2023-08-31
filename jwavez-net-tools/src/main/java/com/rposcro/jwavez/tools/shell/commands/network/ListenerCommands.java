package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NetworkListeningService;
import com.rposcro.jwavez.tools.shell.services.NetworkManagementService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.NETWORK)
public class ListenerCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private NetworkListeningService networkListeningService;

    @ShellMethod(value = "Enters command listener mode", key = "listen")
    public String listenCommand() throws SerialException {
        networkListeningService.startListening();
        console.flushLine("Entered listening mode, press <Enter> to quit");
        console.readLine("");
        networkListeningService.stopListening();
        return "Left listening mode";
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (ShellScope.NETWORK != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }
}
