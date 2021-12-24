package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.shell.services.SerialControllerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
@ShellCommandGroup(CommandGroup.GENERIC)
public class CustomizedQuitCommand implements Quit.Command {

    @Autowired
    private SerialControllerManager serialControllerManager;

    @ShellMethod(value = "Quits JWaveZ shell", key={ "quit", "exit"} )
    public void quit() throws SerialPortException {
        serialControllerManager.releaseAllHooks();
        throw new ExitRequest();
    }
}
