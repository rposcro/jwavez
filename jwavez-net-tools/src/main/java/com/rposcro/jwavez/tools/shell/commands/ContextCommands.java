package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;

@ShellComponent
@ShellCommandGroup(CommandGroup.GENERIC)
public class ContextCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @ShellMethod(value = "Shows context value", key="show")
    public String show(String propertyName) {
        if ("device".equals(propertyName)) {
            if (shellContext.getDevice() == null) {
                return "No current device is set";
            }
            return "Current device is " + shellContext.getDevice();
        } else if ("scope".equals(propertyName)) {
            return "Current working scope is " + shellContext.getShellScope().                          getScopePath();
        }

        return "Don't know what '" + propertyName + "' is ;)";
    }

    @ShellMethod(value = "About")
    public String about() {
        return "JWaveZ Network Shell";
    }

    @ShellMethod(value = "Set current device", key="device")
    public String setCurrentDevice(String pathToDevice) {
        File deviceFile = new File(pathToDevice);
        if (!deviceFile.exists()) {
            return "Incorrect device file! Current device not changed";
        }

        this.shellContext.setDevice(pathToDevice);
        return "Current device changed to " + pathToDevice;
    }
}
