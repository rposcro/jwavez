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
        String nodes = Stream.of(dongleNetworkInformation.getNodeIds())
                .map(nodeId -> "" + nodeId)
                .collect(Collectors.joining(", "));
        return String.format(
                "Network id: %04x\n"
                        + "Dongle node id: %02x\n"
                        + "SUC node id: %02X\n"
                        + "Nodes: %s"
                , dongleNetworkInformation.getNetworkId()
                , dongleNetworkInformation.getDongleNodeId()
                , dongleNetworkInformation.getSucNodeId()
                , nodes
        );
    }

    public String formatRoleInfo(DongleRoleInformation dongleRoleInformation) {
        return String.format(
                "Dongle Node Id: %02x\n"
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
                "Manufacturer id: %04x\n"
                        + "Product type: %04x\n"
                        + "Product id: %04x\n"
                        + "Chip type: %02x\n"
                        + "Chip version: %02x\n"
                        + "App version: %02x\n"
                        + "App revision: %02x\n"
                        + "Library type: %02x\n"
                        + "Version: %02x\n"
                        + "Capabilities: %02x\n"
                        + "Version Response: %s\n"
                        + "Response Data: %02x\n"
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
                    } catch(IllegalArgumentException e) {
                        return String.format("UNKNOWN(%02x)", code);
                    }})
                .collect(Collectors.toList())
        );
    }
}
