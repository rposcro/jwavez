package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.DeviceManagementRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.DeviceStatusRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.DeviceCapabilityRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.NetworkManagementRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.NetworkTransportRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.RemoveNodeFromNetworkRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.SetLearnModeRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.SucRequestBuilder;

public class SerialRequestFactory {

    private AddNodeToNetworkRequestBuilder addNodeToNetworkRequestBuilder;
    private RemoveNodeFromNetworkRequestBuilder removeNodeFromNetworkRequestBuilder;
    private SetLearnModeRequestBuilder setLearnModeRequestBuilder;
    private SucRequestBuilder sucRequestBuilder;
    private DeviceCapabilityRequestBuilder deviceCapabilityRequestBuilder;
    private DeviceStatusRequestBuilder deviceStatusRequestBuilder;
    private DeviceManagementRequestBuilder deviceManagementRequestBuilder;
    private NetworkManagementRequestBuilder networkManagementRequestBuilder;
    private NetworkTransportRequestBuilder networkTransportRequestBuilder;

    public SerialRequestFactory(ByteBufferManager byteBufferManager) {
        this.addNodeToNetworkRequestBuilder = new AddNodeToNetworkRequestBuilder(byteBufferManager);
        this.removeNodeFromNetworkRequestBuilder = new RemoveNodeFromNetworkRequestBuilder(byteBufferManager);
        this.setLearnModeRequestBuilder = new SetLearnModeRequestBuilder(byteBufferManager);
        this.sucRequestBuilder = new SucRequestBuilder(byteBufferManager);
        this.deviceCapabilityRequestBuilder = new DeviceCapabilityRequestBuilder(byteBufferManager);
        this.deviceStatusRequestBuilder = new DeviceStatusRequestBuilder(byteBufferManager);
        this.deviceManagementRequestBuilder = new DeviceManagementRequestBuilder(byteBufferManager);
        this.networkManagementRequestBuilder = new NetworkManagementRequestBuilder(byteBufferManager);
        this.networkTransportRequestBuilder = new NetworkTransportRequestBuilder(byteBufferManager);
    }

    public AddNodeToNetworkRequestBuilder addNodeToNetworkRequestsBuilder() {
        return this.addNodeToNetworkRequestBuilder;
    }

    public RemoveNodeFromNetworkRequestBuilder removeNodeFromNetworkRequestBuilder() {
        return this.removeNodeFromNetworkRequestBuilder;
    }

    public SetLearnModeRequestBuilder setLearnModeRequestBuilder() {
        return this.setLearnModeRequestBuilder;
    }

    public SucRequestBuilder sucRequestBuilder() {
        return this.sucRequestBuilder;
    }

    public DeviceCapabilityRequestBuilder dongleFactsRequestBuilder() {
        return this.deviceCapabilityRequestBuilder;
    }

    public DeviceStatusRequestBuilder deviceStatusRequestBuilder() {
        return this.deviceStatusRequestBuilder;
    }

    public DeviceManagementRequestBuilder deviceManagementRequestBuilder() {
        return this.deviceManagementRequestBuilder;
    }

    public NetworkManagementRequestBuilder networkManagementRequestBuilder() {
        return this.networkManagementRequestBuilder;
    }

    public NetworkTransportRequestBuilder networkTransportRequestBuilder() {
        return this.networkTransportRequestBuilder;
    }
}
