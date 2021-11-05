package com.rposcro.jwavez.tools.shell.models;

import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.responses.GetCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetControllerCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.GetLibraryTypeResponse;
import com.rposcro.jwavez.serial.frames.responses.GetRFPowerLevelResponse;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.GetVersionResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.stream.Collectors;

@Getter
@Builder
public class DongleInformation {

    private MemoryGetIdResponse memoryGetIdResponse;
    private GetSUCNodeIdResponse sucNodeIdResponse;
    private GetControllerCapabilitiesResponse controllerCapabilitiesResponse;
    private GetCapabilitiesResponse capabilitiesResponse;
    private GetInitDataResponse initDataResponse;
    private GetVersionResponse versionResponse;
    private GetLibraryTypeResponse libraryTypeResponse;
    private GetRFPowerLevelResponse rfPowerLevelResponse;

    public String formatNetworkInfo() {
        String nodes = initDataResponse.getNodes().stream()
                .map(nodeId -> "" + nodeId.getId())
                .collect(Collectors.joining(", "));
        return String.format(
                "Home id: %04x\n"
                        + "Dongle node id: %02x\n"
                        + "SUC node id: %02X\n"
                        + "Nodes: %s"
                , memoryGetIdResponse.getHomeId()
                , memoryGetIdResponse.getNodeId().getId()
                , sucNodeIdResponse.getSucNodeId().getId()
                , nodes
        );
    }

    public String formatDongleInfo() {
        return String.format(
                "Dongle Node Id: %02x\n"
                        + "Is real primary: %s\n"
                        + "Is secondary: %s\n"
                        + "Is SUC: %s\n"
                        + "Is SIS: %s\n"
                        + "Is on another network: %s"
                , memoryGetIdResponse.getNodeId().getId()
                , controllerCapabilitiesResponse.isRealPrimary()
                , controllerCapabilitiesResponse.isSecondary()
                , controllerCapabilitiesResponse.isSUC()
                , controllerCapabilitiesResponse.isSIS()
                , controllerCapabilitiesResponse.isOnOtherNetwork()
        );
    }

    public String formatDeviceInfo() {
        return String.format(
                "Manufacturer id: %s\n"
                        + "Product type: %s\n"
                        + "Product id: %s\n"
                        + "Chip type: %s\n"
                        + "Chip version: %s\n"
                        + "App version: %s\n"
                        + "App revision: %s\n"
                        + "Library type: %s\n"
                        + "Version: %s\n"
                        + "Capabilities: %s\n"
                        + "Version: %s\n"
                        + "ZWaveResponse data: %s\n"
                        + "Power level: %s"
                , capabilitiesResponse.getManufacturerId()
                , capabilitiesResponse.getManufacturerProductType()
                , capabilitiesResponse.getManufacturerProductId()
                , initDataResponse.getChipType()
                , initDataResponse.getChipVersion()
                , capabilitiesResponse.getSerialAppVersion()
                , capabilitiesResponse.getSerialAppRevision()
                , libraryTypeResponse.getLibraryType()
                , initDataResponse.getVersion()
                , initDataResponse.getCapabilities()
                , versionResponse.getVersion()
                , versionResponse.getResponseData()
                , rfPowerLevelResponse.getPowerLevel()
        );
    }

    public String formatFunctionsInfo() {
        return String.format("Serial Commands: %s", capabilitiesResponse.getSerialCommands().stream()
                .map(code -> {
                    try {
                        return SerialCommand.ofCode(code.byteValue());
                    } catch(IllegalArgumentException e) {
                        return String.format("UNKNOWN(%02x)", code.byteValue());
                    }})
                .collect(Collectors.toList())
        );
    }
}