package com.rposcro.jwavez.tools.shell.commands.dongle;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.DongleInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.dongle.DongleInformationService;
import com.rposcro.jwavez.tools.shell.services.dongle.DongleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;

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
        availabilityProvider = "dongleAvailability")
    public String showInfo() {
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
        availabilityProvider = "dongleAvailability")
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
            availabilityProvider = "dongleAvailability")
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
}
