package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NetworkListeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NETWORK)
public class ListenerCommands {

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private NetworkListeningService networkListeningService;

    @Command(name = "listen", description = "Enters command listener mode",
        availabilityProvider = "dongleAvailability")
    public String listenCommand() throws SerialException {
        networkListeningService.startListening();
        console.flushLine("Entered listening mode, press <Enter> to quit");
        console.readLine("");
        networkListeningService.stopListening();
        return "Left listening mode";
    }
}
