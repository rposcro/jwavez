package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NetworkListeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@Command(group = CommandGroup.NETWORK)
public class ListenerCommands {

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private NetworkListeningService networkListeningService;

    @Command(command = "listen", description = "Enters command listener mode")
    @CommandAvailability(provider = "dongleAvailability")
    public String listenCommand() throws SerialException {
        networkListeningService.startListening();
        console.flushLine("Entered listening mode, press <Enter> to quit");
        console.readLine("");
        networkListeningService.stopListening();
        return "Left listening mode";
    }
}
