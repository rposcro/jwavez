package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.SetDefaultRequest;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DongleManagementService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    public void resetToFactoryDefaults() throws SerialException {
        serialCommunicationService.runBasicSynchronousFunction(controller -> {
            controller.requestCallbackFlow(SetDefaultRequest.createSetDefaultRequest(SerialUtils.nextFlowId()));
            return true;
        });
    }
}
