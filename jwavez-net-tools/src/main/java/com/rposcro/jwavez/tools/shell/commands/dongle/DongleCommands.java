package com.rposcro.jwavez.tools.shell.commands.dongle;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.DongleInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.DongleCheckService;
import com.rposcro.jwavez.tools.shell.services.DongleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
@ShellCommandGroup(CommandGroup.DONGLE)
public class DongleCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private DongleCheckService dongleCheckService;

    @Autowired
    private DongleManagementService dongleManagementService;

    @Autowired
    private DongleInformationFormatter dongleInformationFormatter;

    @Autowired
    private ConsoleAccessor console;

    @ShellMethod(value = "Shows current dongle information", key="info")
    public String showInfo() throws SerialException {
        DongleInformation dongleInformation = dongleCheckService.collectDongleInformation();
        return String.format("** Network Information\n%s\n\n"
                + "** Dongle Role Information\n%s\n\n"
                + "** Device Information\n%s\n\n"
                + "** Functions\n%s\n"
                , dongleInformationFormatter.formatNetworkInfo(dongleInformation.getDongleNetworkInformation())
                , dongleInformationFormatter.formatRoleInfo(dongleInformation.getDongleRoleInformation())
                , dongleInformationFormatter.formatDeviceInfo(dongleInformation.getDongleDeviceInformation())
                , dongleInformationFormatter.formatFunctionsInfo(dongleInformation.getDongleDeviceInformation().getSerialCommandIds())
        );
    }

    @ShellMethod(value = "Reset dongle to factory defaults", key="wipeout")
    public String factoryReset() throws SerialException {
        String answer = console.readLine("NOTE!\n"
                + "If you continue, ALL device settings will be reset to factory defaults and your custom changes will be lost.\n"
                + "All nodes known by this dongle will be forgotten unless they are shared with another dongle controller.\n"
                + "Do you want to continue? If so, say 'continue':\n>"
        );

        if ("continue".equalsIgnoreCase(answer)) {
            console.flushLine("Dongle is being reset, please wait and do not interrupt the process...");
            dongleManagementService.resetToFactoryDefaults();
            return "Reset COMPLETE\n";
        } else {
            return "Reset cancelled\n";
        }
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (ShellScope.DONGLE != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDevice() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }
}
