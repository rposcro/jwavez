package com.rposcro.jwavez.tools.shell.formatters;

import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.tools.shell.models.DongleDeviceInformation;
import com.rposcro.jwavez.tools.shell.models.DongleNetworkInformation;
import com.rposcro.jwavez.tools.shell.models.DongleRoleInformation;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DongleInformationFormatter {

    public String formatNetworkInfo(DongleNetworkInformation dongleNetworkInformation) {
        String nodes = Arrays.stream(dongleNetworkInformation.getNodeIds())
                .mapToObj(nodeId -> String.format("%02X", nodeId))
                .collect(Collectors.joining(", "));
        return String.format(
                "Network id: 0x%04X\n"
                        + "Dongle node id: 0x%02X\n"
                        + "SUC node id: 0x%02X\n"
                        + "Nodes: %s"
                , dongleNetworkInformation.getNetworkId()
                , dongleNetworkInformation.getDongleNodeId()
                , dongleNetworkInformation.getSucNodeId()
                , nodes
        );
    }

    public String formatRoleInfo(DongleRoleInformation dongleRoleInformation) {
        return String.format(
                "Dongle Node Id: 0x%02X\n"
                        + "Is real primary: %s\n"
                        + "Is secondary: %s\n"
                        + "Is SUC: %s\n"
                        + "Is SIS: %s\n"
                        + "Is on another network: %s"
                , dongleRoleInformation.getDongleNodeId()
                , dongleRoleInformation.isRealPrimary()
                , dongleRoleInformation.isSecondary()
                , dongleRoleInformation.isSUC()
                , dongleRoleInformation.isSIS()
                , dongleRoleInformation.isOnOtherNetwork()
        );
    }

    public String formatDeviceInfo(DongleDeviceInformation dongleDeviceInformation) {
        return String.format(
                "Manufacturer id: 0x%04X\n"
                        + "Product type: 0x%04X\n"
                        + "Product id: 0x%04X\n"
                        + "Chip type: 0x%02X\n"
                        + "Chip version: 0x%02X\n"
                        + "App version: 0x%02X\n"
                        + "App revision: 0x%02X\n"
                        + "Library type: %s\n"
                        + "Version: 0x%02X\n"
                        + "Capabilities: 0x%02X\n"
                        + "Version Response: %s\n"
                        + "Version Response Data: 0x%02X"
                , dongleDeviceInformation.getManufacturerId()
                , dongleDeviceInformation.getProductType()
                , dongleDeviceInformation.getProductId()
                , dongleDeviceInformation.getChipType()
                , dongleDeviceInformation.getChipVersion()
                , dongleDeviceInformation.getAppVersion()
                , dongleDeviceInformation.getAppRevision()
                , dongleDeviceInformation.getLibraryType()
                , dongleDeviceInformation.getVersion()
                , dongleDeviceInformation.getCapabilities()
                , dongleDeviceInformation.getVersionResponse()
                , dongleDeviceInformation.getDataResponse()
        );
    }

    public String formatFunctionsInfo(int[] commandCodes) {
        return String.format("Serial Commands: %s", Arrays.stream(commandCodes)
                .mapToObj(code -> {
                    try {
                        return SerialCommand.ofCode((byte) code);
                    } catch (IllegalArgumentException e) {
                        return String.format("UNKNOWN(0x%02X)", code);
                    }
                })
                .collect(Collectors.toList())
        );
    }
}
