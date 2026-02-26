package com.rposcro.jwavez.tools.shell.commands.generic;

import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@Command(group = CommandGroup.GENERIC)
public class CustomizedQuitCommand implements Quit.Command {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Command(command = "quit", description = "Quits JWaveZ shell")
    public void quit() throws SerialPortException {
        serialCommunicationService.releaseAllHooks();
        throw new ExitRequest();
    }
}
