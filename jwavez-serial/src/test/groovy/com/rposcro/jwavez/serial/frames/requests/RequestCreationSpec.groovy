package com.rposcro.jwavez.serial.frames.requests

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand
import com.rposcro.jwavez.core.model.NodeId
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode
import com.rposcro.jwavez.serial.model.LearnMode
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode
import spock.lang.Specification

class RequestCreationSpec extends Specification {

    def "requests are created without exception"() {
        expect:
        null != EnableSUCRequest.createEnableSUCRequest();
        null != GetCapabilitiesRequest.createGetCapabilitiesRequest();
        null != GetControllerCapabilitiesRequest.createGetControllerCapabiltiesRequest();
        null != GetInitDataRequest.createGetInitDataRequest();
        null != GetLibraryTypeRequest.createLibraryTypeRequest();
        null != GetNetworkStatsRequest.createGetNetworkStatsRequest();
        null != GetProtocolStatusRequest.createGetProtocolStatusRequest();
        null != GetRFPowerLevelRequest.createGetRFPowerLevelRequest();
        null != GetSUCNodeIdRequest.createGetSUCNodeIdRequest();
        null != GetVersionRequest.createGetVersionRequest();
        null != MemoryGetIdRequest.createMemoryGetIdRequest();
        null != SendDataAbortRequest.createSendDataAbortRequest();
        null != SerialAPISetupRequest.createEnableStatusReportRequest();
        null != SetDefaultRequest.createSetDefaultRequest((byte) 0x78);
        null != RequestNodeInfoRequest.createRequestNodeInfoRequest(new NodeId(0x12));
        null != AddNodeToNetworkRequest.createAddNodeToNetworkRequest(AddNodeToNeworkMode.ADD_NODE_ANY, (byte) 0x44, true, false);
        null != RemoveNodeFromNetworkRequest.createRemoveNodeFromNetworkRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_ANY, (byte) 0x33, true);
        null != SendDataRequest.createSendDataRequest(new NodeId(0x55), new ZWaveControlledCommand((byte) 0x11), (byte) 0x77);
        null != SendSUCIdRequest.createSendSUCIdRequest(new NodeId(0x12), (byte) 0x23);
        null != SetLearnModeRequest.createSetLearnModeRequest(LearnMode.LEARN_MODE_CLASSIC, (byte) 0x77);
        null != SetSUCNodeIdRequest.createSetLocalSUCNodeRequest(new NodeId(0x11), false);
        null != SetSUCNodeIdRequest.createSetLocalSUCNodeRequest(new NodeId(0x66), true);
    }
}
