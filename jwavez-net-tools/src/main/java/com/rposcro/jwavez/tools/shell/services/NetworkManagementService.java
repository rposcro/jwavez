package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkManagementService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    public Integer runInclusionMode(long timeoutInMilliseconds) throws SerialException {
        NodeInfo nodeInfo = serialCommunicationService.runNodeInclusion(timeoutInMilliseconds);
        return nodeInfo == null ? null : (int) nodeInfo.getId().getId();
    }

    public Integer runExclusionMode(long timeoutInMilliseconds) throws SerialException {
        NodeInfo nodeInfo = serialCommunicationService.runNodeExclusion(timeoutInMilliseconds);
        return nodeInfo == null ? null : (int) nodeInfo.getId().getId();
    }
}
