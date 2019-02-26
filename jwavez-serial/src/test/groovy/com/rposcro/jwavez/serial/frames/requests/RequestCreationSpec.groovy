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
        null != EnableSUCRequest.createSerialRequest();
        null != GetCapabilitiesRequest.createSerialRequest();
        null != GetControllerCapabilitiesRequest.createSerialRequest();
        null != GetInitDataRequest.createSerialRequest();
        null != GetLibraryTypeRequest.createSerialRequest();
        null != GetNetworkStatsRequest.createSerialRequest();
        null != GetProtocolStatusRequest.createSerialRequest();
        null != GetRFPowerLevelRequest.createSerialRequest();
        null != GetSUCNodeIdRequest.createSerialRequest();
        null != GetVersionRequest.createSerialRequest();
        null != MemoryGetIdRequest.createSerialRequest();
        null != SendDataAbortRequest.createSerialRequest();
        null != SerialAPISetupRequest.createEnableStatusReportRequest();
        null != SetDefaultRequest.createSerialRequest((byte) 0x78);
        null != RequestNodeInfoRequest.createSerialRequest(new NodeId(0x12));
        null != AddNodeToNetworkRequest.createSerialRequest(AddNodeToNeworkMode.ADD_NODE_ANY, (byte) 0x44, true, false);
        null != RemoveNodeFromNetworkRequest.createSerialRequest(RemoveNodeFromNeworkMode.REMOVE_NODE_ANY, (byte) 0x33, true);
        null != SendDataRequest.createSendDataRequest(new NodeId(0x55), new ZWaveControlledCommand((byte) 0x11), (byte) 0x77);
        null != SendSUCIdRequest.createSerialRequest(new NodeId(0x12), (byte) 0x23);
        null != SetLearnModeRequest.createSerialRequest(LearnMode.LEARN_MODE_CLASSIC, (byte) 0x77);
        null != SetSUCNodeIdRequest.createSetLocalSUCNodeRequest(new NodeId(0x11), false);
        null != SetSUCNodeIdRequest.createSetLocalSUCNodeRequest(new NodeId(0x66), true);
    }
}
