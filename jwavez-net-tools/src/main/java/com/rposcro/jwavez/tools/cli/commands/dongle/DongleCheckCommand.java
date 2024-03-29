package com.rposcro.jwavez.tools.cli.commands.dongle;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.responses.GetCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetControllerCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.GetLibraryTypeResponse;
import com.rposcro.jwavez.serial.frames.responses.GetRFPowerLevelResponse;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.GetVersionResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.commands.AbstractSyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;
import com.rposcro.jwavez.tools.cli.utils.SerialProcedure;

import java.util.stream.Collectors;

public class DongleCheckCommand extends AbstractSyncBasedCommand {

    private DongleCheckOptions options;

    @Override
    public void configure(String[] args) throws CommandOptionsException {
        options = new DongleCheckOptions(args);
    }

    @Override
    public void execute() {
        System.out.println("Checking dongle " + options.getDevice() + "...\n");
        ProcedureUtil.executeProcedure(this::runChecks);
        System.out.println("Checking dongle finished");
    }

    private void runChecks() throws SerialException {
        connect(options);
        runCheck(this::runNetworkIds, options.runNetworkIds(), "Network IDs");
        runCheck(this::runSUCId, options.runSucId(), "SUC Id");
        runCheck(this::runControllerCapabilities, options.runControllerCapabilities(), "Controller Capabilities");
        runCheck(this::runCapabilities, options.runCapabilities(), "Device Capabilities");
        runCheck(this::runInitialData, options.runInitialData(), "Initial Data");
        runCheck(this::runGetVersion, options.runGetVersion(), "Version");
        runCheck(this::runLibraryType, options.runLibraryType(), "Library Type");
        runCheck(this::runPowerLevel, options.runPowerLevel(), "Power Level");
    }

    private void runCheck(SerialProcedure procedure, boolean activate, String header) throws SerialException {
        if (activate) {
            sectionHeader(header);
            procedure.execute();
            sectionFooter();
            System.out.println();
        }
    }

    private void runSUCId() throws SerialException {
        GetSUCNodeIdResponse response = controller.requestResponseFlow(serialRequestFactory.sucRequestBuilder().createGetSUCNodeIdRequest());
        System.out.printf("  SUC node id: %02X\n", response.getSucNodeId().getId());
    }

    private void runPowerLevel() throws SerialException {
        GetRFPowerLevelResponse response = controller.requestResponseFlow(
                serialRequestFactory.deviceStatusRequestBuilder().createGetRFPowerLevelRequest());
        System.out.printf("  Power level: %s\n", response.getPowerLevel());
    }

    private void runNetworkIds() throws SerialException {
        MemoryGetIdResponse response = controller.requestResponseFlow(
                serialRequestFactory.dongleFactsRequestBuilder().createMemoryGetIdRequest());
        System.out.printf("  HomeId: %04x\n", response.getHomeId());
        System.out.printf("  Dongle NodeId: %02x\n", response.getNodeId().getId());
    }

    private void runLibraryType() throws SerialException {
        GetLibraryTypeResponse response = controller.requestResponseFlow(
                serialRequestFactory.dongleFactsRequestBuilder().createGetLibraryTypeRequest());
        System.out.printf("  Library type: %s\n", response.getLibraryType());
    }

    private void runInitialData() throws SerialException {
        GetInitDataResponse response = controller.requestResponseFlow(
                serialRequestFactory.dongleFactsRequestBuilder().createGetInitDataRequest());
        System.out.printf("  Version: %s\n", response.getVersion());
        System.out.printf("  Capabilities: %s\n", response.getCapabilities());
        System.out.printf("  Chip type: %s\n", response.getChipType());
        System.out.printf("  Chip version: %s\n", response.getChipVersion());
        System.out.printf("  Nodes: %s\n", response.getNodes().stream()
                .map(NodeId::getId)
                .collect(Collectors.toList()));
    }

    private void runGetVersion() throws SerialException {
        GetVersionResponse response = controller.requestResponseFlow(
                serialRequestFactory.dongleFactsRequestBuilder().createGetVersionRequest());
        System.out.printf("  Version: %s\n", response.getVersion());
        System.out.printf("  ZWaveResponse data: %s\n", response.getResponseData());
    }

    private void runControllerCapabilities() throws SerialException {
        GetControllerCapabilitiesResponse response = controller.requestResponseFlow(
                serialRequestFactory.dongleFactsRequestBuilder().createGetControllerCapabilitiesRequest());
        System.out.printf("  Is real primary: %s\n", response.isRealPrimary());
        System.out.printf("  Is secondary: %s\n", response.isSecondary());
        System.out.printf("  Is SUC: %s\n", response.isSUC());
        System.out.printf("  Is SIS: %s\n", response.isSIS());
        System.out.printf("  Is on another network: %s\n", response.isOnOtherNetwork());
    }

    private void runCapabilities() throws SerialException {
        GetCapabilitiesResponse response = controller.requestResponseFlow(
                serialRequestFactory.dongleFactsRequestBuilder().createGetCapabilitiesRequest());
        System.out.printf("  App version: %s\n", response.getSerialAppVersion());
        System.out.printf("  App revision: %s\n", response.getSerialAppRevision());
        System.out.printf("  Manufacturer id: %s\n", response.getManufacturerId());
        System.out.printf("  Product type: %s\n", response.getManufacturerProductType());
        System.out.printf("  Product id: %s\n", response.getManufacturerProductId());
        System.out.printf("  Functions: %s\n", response.getSerialCommands().stream()
                .map(code -> {
                    try {
                        return SerialCommand.ofCode(code.byteValue());
                    } catch (IllegalArgumentException e) {
                        return String.format("UNKNOWN(%02x)", code.byteValue());
                    }
                })
                .collect(Collectors.toList()));
    }

    private void sectionHeader(String sectionName) {
        System.out.println(":: " + sectionName);
    }

    private void sectionFooter() {
        //System.out.println(":: ");
    }

    private void sectionFailure() {
        System.out.println("!! Error occurred");
    }

    public static void main(String... args) throws Exception {
        ZWaveCLI.main("info", "-d", "/dev/tty.usbmodem1421");
    }
}
