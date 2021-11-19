package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkManagementService {

    @Autowired
    private SerialControllerManager serialControllerManager;

    public Integer runInclusionMode(long timeoutInMilliseconds) throws SerialException {
        NodeInfo nodeInfo = serialControllerManager.runNodeInclusion(timeoutInMilliseconds);
        return nodeInfo == null ? null : (int) nodeInfo.getId().getId();
    }

    public Integer runExclusionMode(long timeoutInMilliseconds) throws SerialException {
        NodeInfo nodeInfo = serialControllerManager.runNodeExclusion(timeoutInMilliseconds);
        return nodeInfo == null ? null : (int) nodeInfo.getId().getId();
    }
}
