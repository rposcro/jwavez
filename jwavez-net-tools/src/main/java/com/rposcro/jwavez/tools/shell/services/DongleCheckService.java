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
import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DongleCheckService {

    @Autowired
    private SerialControllerManager controllerManager;

    public DongleInformation collectDongleInformation() throws SerialException {
        return DongleInformation.builder()
                .sucNodeIdResponse(askForSUCId())
                .capabilitiesResponse(askForDongleCapabilities())
                .controllerCapabilitiesResponse(askForControllerCapabilities())
                .initDataResponse(askForInitialData())
                .memoryGetIdResponse(askForNetworkIds())
                .libraryTypeResponse(askForLibraryType())
                .versionResponse(askForVersion())
                .rfPowerLevelResponse(askForRFPowerLevel())
                .build();
    }

    public GetLibraryTypeResponse askForLibraryType() throws SerialException {
        SerialFunction<BasicSynchronousController, GetLibraryTypeResponse> function = (controller) -> {
            GetLibraryTypeResponse response = controller.requestResponseFlow(GetLibraryTypeRequest.createLibraryTypeRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public GetRFPowerLevelResponse askForRFPowerLevel() throws SerialException {
        SerialFunction<BasicSynchronousController, GetRFPowerLevelResponse> function = (controller) -> {
            GetRFPowerLevelResponse response = controller.requestResponseFlow(GetRFPowerLevelRequest.createGetRFPowerLevelRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public GetVersionResponse askForVersion() throws SerialException {
        SerialFunction<BasicSynchronousController, GetVersionResponse> function = (controller) -> {
            GetVersionResponse response = controller.requestResponseFlow(GetVersionRequest.createGetVersionRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public MemoryGetIdResponse askForNetworkIds() throws SerialException {
        SerialFunction<BasicSynchronousController, MemoryGetIdResponse> function = (controller) -> {
            MemoryGetIdResponse response = controller.requestResponseFlow(MemoryGetIdRequest.createMemoryGetIdRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public GetSUCNodeIdResponse askForSUCId() throws SerialException {
        SerialFunction<BasicSynchronousController, GetSUCNodeIdResponse> function = (controller) -> {
            GetSUCNodeIdResponse response = controller.requestResponseFlow(GetSUCNodeIdRequest.createGetSUCNodeIdRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public GetControllerCapabilitiesResponse askForControllerCapabilities() throws SerialException {
        SerialFunction<BasicSynchronousController, GetControllerCapabilitiesResponse> function = (controller) -> {
            GetControllerCapabilitiesResponse response = controller.requestResponseFlow(GetControllerCapabilitiesRequest.createGetControllerCapabiltiesRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public GetCapabilitiesResponse askForDongleCapabilities() throws SerialException {
        SerialFunction<BasicSynchronousController, GetCapabilitiesResponse> function = (controller) -> {
            GetCapabilitiesResponse response = controller.requestResponseFlow(GetCapabilitiesRequest.createGetCapabilitiesRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }

    public GetInitDataResponse askForInitialData() throws SerialException {
        SerialFunction<BasicSynchronousController, GetInitDataResponse> function = (controller) -> {
            GetInitDataResponse response = controller.requestResponseFlow(GetInitDataRequest.createGetInitDataRequest());
            return response;
        };
        return controllerManager.runBasicSynchronousFunction(function);
    }
}
