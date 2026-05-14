package com.rposcro.jwavez.tools.shell.commands.dongle;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.DongleInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.dongle.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.dongle.DongleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.DONGLE)
public class DongleCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private DongleManagementService dongleManagementService;

    @Autowired
    private DongleInformationService dongleInformationService;

    @Autowired
    private DongleInformationFormatter dongleInformationFormatter;

    @Autowired
    private ConsoleAccessor console;

    @Command(name = "info", description = "Show current dongle information",
        availabilityProvider = "dongleCommandsAvailability")
    public String showInfo() throws SerialException {
        DongleInformation dongleInformation = shellContext.getDongleInformation();
        return String.format("\n** Network Information\n%s\n\n"
                        + "** Dongle Role Information\n%s\n\n"
                        + "** Device Information\n%s\n\n"
                        + "** Functions\n%s\n"
                , dongleInformationFormatter.formatNetworkInfo(dongleInformation.getDongleNetworkInformation())
                , dongleInformationFormatter.formatRoleInfo(dongleInformation.getDongleRoleInformation())
                , dongleInformationFormatter.formatDeviceInfo(dongleInformation.getDongleDeviceInformation())
                , dongleInformationFormatter.formatFunctionsInfo(dongleInformation.getDongleCommandInformation().getSupportedSerialCommandIds())
        );
    }

    @Command(name = "fetch", description = "Fetches dongle information from the device",
        availabilityProvider = "dongleCommandsAvailability")
    public String fetchInfo() throws SerialException {
        DongleInformation dongleInformation = dongleInformationService.collectDongleInformation();
        shellContext.setDongleInformation(dongleInformation);
        return String.format("\n** Network Information\n%s\n\n"
                        + "** Dongle Role Information\n%s\n\n"
                        + "** Device Information\n%s\n\n"
                        + "** Functions\n%s\n"
                , dongleInformationFormatter.formatNetworkInfo(dongleInformation.getDongleNetworkInformation())
                , dongleInformationFormatter.formatRoleInfo(dongleInformation.getDongleRoleInformation())
                , dongleInformationFormatter.formatDeviceInfo(dongleInformation.getDongleDeviceInformation())
                , dongleInformationFormatter.formatFunctionsInfo(dongleInformation.getDongleCommandInformation().getSupportedSerialCommandIds())
        );
    }

    @Command(name = "wipeout", description = "Reset dongle to factory defaults",
            availabilityProvider = "dongleCommandsAvailability")
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

    @Bean
    public AvailabilityProvider dongleCommandsAvailability() {

        Availability availability;

        if (ShellScope.DONGLE != shellContext.getScopeContext().getScope()) {
            availability = Availability.unavailable("Command not available in current scope");
        } else {
            availability = shellContext.getDongleDevicePath() != null ?
                    Availability.available() :
                    Availability.unavailable("ZWave dongle device is not specified");
        }

        return AvailabilityProvider.of(availability);
    }
}
