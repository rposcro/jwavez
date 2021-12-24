package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.GetCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetControllerCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetInitDataRequest;
import com.rposcro.jwavez.serial.frames.requests.GetLibraryTypeRequest;
import com.rposcro.jwavez.serial.frames.requests.GetRFPowerLevelRequest;
import com.rposcro.jwavez.serial.frames.requests.GetSUCNodeIdRequest;
import com.rposcro.jwavez.serial.frames.requests.GetVersionRequest;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.GetCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetControllerCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.GetLibraryTypeResponse;
import com.rposcro.jwavez.serial.frames.responses.GetRFPowerLevelResponse;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.GetVersionResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.DongleCommandInformation;
import com.rposcro.jwavez.tools.shell.models.DongleDeviceInformation;
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.models.DongleNetworkInformation;
import com.rposcro.jwavez.tools.shell.models.DongleRoleInformation;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DongleInformationService {

    @Autowired
    private SerialControllerManager controllerManager;

    @Autowired
    private JWaveZShellContext shellContext;

    public DongleInformation collectDongleInformation() throws SerialException {
        GetSUCNodeIdResponse sucNodeIdResponse = askForSUCId();
        GetInitDataResponse getInitDataResponse = askForInitialData();
        MemoryGetIdResponse memoryGetIdResponse = askForNetworkIds();
        GetControllerCapabilitiesResponse getControllerCapabilitiesResponse = askForControllerCapabilities();
        GetCapabilitiesResponse getCapabilitiesResponse = askForDongleCapabilities();
        GetLibraryTypeResponse getLibraryTypeResponse = askForLibraryType();
        GetVersionResponse getVersionResponse = askForVersion();

        return DongleInformation.builder()
                .dongleNetworkInformation(createNetworkInformation(memoryGetIdResponse, getInitDataResponse, sucNodeIdResponse))
                .dongleRoleInformation(createRoleInformation(memoryGetIdResponse, getControllerCapabilitiesResponse))
                .dongleDeviceInformation(createDeviceInformation(
                        getCapabilitiesResponse, getInitDataResponse, getLibraryTypeResponse, getVersionResponse))
                .dongleCommandInformation(createCommandInformation(getCapabilitiesResponse))
                .build();
    }

    public DongleNetworkInformation collectDongleNetworkInformation() throws SerialException {
        GetSUCNodeIdResponse sucNodeIdResponse = askForSUCId();
        GetInitDataResponse getInitDataResponse = askForInitialData();
        MemoryGetIdResponse memoryGetIdResponse = askForNetworkIds();
        return createNetworkInformation(memoryGetIdResponse, getInitDataResponse, sucNodeIdResponse);
    }

    public boolean matchesCurrentDongle(DongleDeviceInformation dongleDeviceInformation) {
        DongleDeviceInformation currentDeviceInformation = shellContext.getDongleInformation().getDongleDeviceInformation();
        return currentDeviceInformation.getCapabilities() == dongleDeviceInformation.getCapabilities()
                && currentDeviceInformation.getDataResponse() == dongleDeviceInformation.getDataResponse()
                && currentDeviceInformation.getVersionResponse().equals(dongleDeviceInformation.getVersionResponse())
                && currentDeviceInformation.getChipType() == dongleDeviceInformation.getChipType()
                && currentDeviceInformation.getChipVersion() == dongleDeviceInformation.getChipVersion()
                && currentDeviceInformation.getAppVersion() == dongleDeviceInformation.getAppVersion()
                && currentDeviceInformation.getAppRevision() == dongleDeviceInformation.getAppRevision()
                && currentDeviceInformation.getVersion() == dongleDeviceInformation.getVersion()
                && currentDeviceInformation.getManufacturerId() == dongleDeviceInformation.getManufacturerId()
                && currentDeviceInformation.getLibraryType() == dongleDeviceInformation.getLibraryType()
                && currentDeviceInformation.getProductId() == dongleDeviceInformation.getProductId()
                && currentDeviceInformation.getProductType() == dongleDeviceInformation.getProductType()
                ;
    }

    private DongleDeviceInformation createDeviceInformation(
            GetCapabilitiesResponse getCapabilitiesResponse,
            GetInitDataResponse getInitDataResponse,
            GetLibraryTypeResponse getLibraryTypeResponse,
            GetVersionResponse getVersionResponse
    ) {
        return DongleDeviceInformation.builder()
                .manufacturerId(getCapabilitiesResponse.getManufacturerId())
                .productType(getCapabilitiesResponse.getManufacturerProductType())
                .productId(getCapabilitiesResponse.getManufacturerProductId())
                .appVersion(getCapabilitiesResponse.getSerialAppVersion())
                .appRevision(getCapabilitiesResponse.getSerialAppRevision())
                .chipType(getInitDataResponse.getChipType())
                .chipVersion(getInitDataResponse.getChipVersion())
                .version(getInitDataResponse.getVersion())
                .capabilities(getInitDataResponse.getCapabilities())
                .libraryType(getLibraryTypeResponse.getLibraryType())
                .versionResponse(getVersionResponse.getVersion())
                .dataResponse(getVersionResponse.getResponseData())
                .build();
    }

    private DongleCommandInformation createCommandInformation(GetCapabilitiesResponse getCapabilitiesResponse) {
        return DongleCommandInformation.builder()
                .supportedSerialCommandIds(getCapabilitiesResponse.getSerialCommands().stream().mapToInt(Integer::intValue).toArray())
                .build();
    }

    private DongleRoleInformation createRoleInformation(
            MemoryGetIdResponse memoryGetIdResponse,
            GetControllerCapabilitiesResponse controllerCapabilitiesResponse
    ) {
        return DongleRoleInformation.builder()
                .dongleNodeId(memoryGetIdResponse.getNodeId().getId())
                .isRealPrimary(controllerCapabilitiesResponse.isRealPrimary())
                .isSecondary(controllerCapabilitiesResponse.isSecondary())
                .isSUC(controllerCapabilitiesResponse.isSUC())
                .isSIS(controllerCapabilitiesResponse.isSIS())
                .isOnOtherNetwork(controllerCapabilitiesResponse.isOnOtherNetwork())
                .build();
    }

    private DongleNetworkInformation createNetworkInformation(
            MemoryGetIdResponse memoryGetIdResponse,
            GetInitDataResponse getInitDataResponse,
            GetSUCNodeIdResponse sucNodeIdResponse
    ) {
        return DongleNetworkInformation.builder()
                .networkId(memoryGetIdResponse.getHomeId())
                .dongleNodeId(memoryGetIdResponse.getNodeId().getId())
                .sucNodeId(sucNodeIdResponse.getSucNodeId().getId())
                .nodeIds(getInitDataResponse.getNodes().stream().mapToInt(id -> (int) id.getId()).toArray())
                .build();
    }

    private GetLibraryTypeResponse askForLibraryType() throws SerialException {
        SerialFunction<BasicSynchronousController, GetLibraryTypeResponse> function = (controller) -> {
            GetLibraryTypeResponse response = controller.requestResponseFlow(GetLibraryTypeRequest.createLibraryTypeRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private GetRFPowerLevelResponse askForRFPowerLevel() throws SerialException {
        SerialFunction<BasicSynchronousController, GetRFPowerLevelResponse> function = (controller) -> {
            GetRFPowerLevelResponse response = controller.requestResponseFlow(GetRFPowerLevelRequest.createGetRFPowerLevelRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private GetVersionResponse askForVersion() throws SerialException {
        SerialFunction<BasicSynchronousController, GetVersionResponse> function = (controller) -> {
            GetVersionResponse response = controller.requestResponseFlow(GetVersionRequest.createGetVersionRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private MemoryGetIdResponse askForNetworkIds() throws SerialException {
        SerialFunction<BasicSynchronousController, MemoryGetIdResponse> function = (controller) -> {
            MemoryGetIdResponse response = controller.requestResponseFlow(MemoryGetIdRequest.createMemoryGetIdRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private GetSUCNodeIdResponse askForSUCId() throws SerialException {
        SerialFunction<BasicSynchronousController, GetSUCNodeIdResponse> function = (controller) -> {
            GetSUCNodeIdResponse response = controller.requestResponseFlow(GetSUCNodeIdRequest.createGetSUCNodeIdRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private GetControllerCapabilitiesResponse askForControllerCapabilities() throws SerialException {
        SerialFunction<BasicSynchronousController, GetControllerCapabilitiesResponse> function = (controller) -> {
            GetControllerCapabilitiesResponse response = controller.requestResponseFlow(GetControllerCapabilitiesRequest.createGetControllerCapabiltiesRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private GetCapabilitiesResponse askForDongleCapabilities() throws SerialException {
        SerialFunction<BasicSynchronousController, GetCapabilitiesResponse> function = (controller) -> {
            GetCapabilitiesResponse response = controller.requestResponseFlow(GetCapabilitiesRequest.createGetCapabilitiesRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    private GetInitDataResponse askForInitialData() throws SerialException {
        SerialFunction<BasicSynchronousController, GetInitDataResponse> function = (controller) -> {
            GetInitDataResponse response = controller.requestResponseFlow(GetInitDataRequest.createGetInitDataRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }
}
