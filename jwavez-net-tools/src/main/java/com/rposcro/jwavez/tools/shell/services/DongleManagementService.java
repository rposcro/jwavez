package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DongleManagementService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private SerialRequestFactory serialRequestFactory;

    public void resetToFactoryDefaults() throws SerialException {
        serialCommunicationService.runBasicSynchronousFunction(controller -> {
            serialRequestFactory.deviceManagementRequestBuilder().createSetDefaultRequest(SerialUtils.nextFlowId());
            return true;
        });
    }
}
