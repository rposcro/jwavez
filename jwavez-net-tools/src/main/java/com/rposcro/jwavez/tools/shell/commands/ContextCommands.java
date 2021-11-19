package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
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

    @ShellMethod(value = "Show context value {device, scope}", key="show")
    public String show(@ShellOption(value = { "--property-name", "-pname" }) String propertyName) {
        if ("device".equals(propertyName)) {
            if (shellContext.getDevice() == null) {
                return "No current device is set";
            }
            return "Current device is " + shellContext.getDevice();
        } else if ("scope".equals(propertyName)) {
            return "Current working scope is " + shellContext.getShellScope().getScopePath();
        }

        return "Don't know what '" + propertyName + "' is, chose from {device|scope}";
    }

    @ShellMethod(value = "About")
    public String about() {
        return "JWaveZ Network Shell";
    }

    @ShellMethod(value = "Set current device", key="device")
    public String setCurrentDevice(@ShellOption(value = { "--path-to-device", "-path" }) String pathToDevice) {
        File deviceFile = new File(pathToDevice);
        if (!deviceFile.exists()) {
            return "Incorrect device file! Current device not changed";
        }

        this.shellContext.setDevice(pathToDevice);
        return "Current device changed to " + pathToDevice;
    }
}
