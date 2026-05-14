package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NetworkListeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NETWORK)
public class ListenerCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private NetworkListeningService networkListeningService;

    @Command(name = "listen", description = "Enters command listener mode",
        availabilityProvider = "listenerCommandsAvailability")
    public String listenCommand() throws SerialException {
        networkListeningService.startListening();
        console.flushLine("Entered listening mode, press <Enter> to quit");
        console.readLine("");
        networkListeningService.stopListening();
        return "Left listening mode";
    }

    @Bean
    public AvailabilityProvider listenerCommandsAvailability() {

        Availability availability;

        if (ShellScope.NETWORK != shellContext.getScopeContext().getScope()) {
            availability = Availability.unavailable("Command not available in current scope");
        } else {
            availability = shellContext.getDongleDevicePath() != null ?
                    Availability.available() :
                    Availability.unavailable("ZWave dongle device is not specified");
        }

        return AvailabilityProvider.of(availability);
    }
}
