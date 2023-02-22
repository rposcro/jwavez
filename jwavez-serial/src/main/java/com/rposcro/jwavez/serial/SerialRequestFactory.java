package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.DongleFactsRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.RemoveNodeFromNetworkRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.SetLearnModeRequestBuilder;
import com.rposcro.jwavez.serial.frames.requests.SucRequestBuilder;

public class SerialRequestFactory {

    private AddNodeToNetworkRequestBuilder addNodeToNetworkRequestBuilder;
    private RemoveNodeFromNetworkRequestBuilder removeNodeFromNetworkRequestBuilder;
    private SetLearnModeRequestBuilder setLearnModeRequestBuilder;
    private SucRequestBuilder sucRequestBuilder;
    private DongleFactsRequestBuilder dongleFactsRequestBuilder;

    public SerialRequestFactory() {
        this.addNodeToNetworkRequestBuilder = new AddNodeToNetworkRequestBuilder();
        this.removeNodeFromNetworkRequestBuilder = new RemoveNodeFromNetworkRequestBuilder();
        this.setLearnModeRequestBuilder = new SetLearnModeRequestBuilder();
        this.sucRequestBuilder = new SucRequestBuilder();
        this.dongleFactsRequestBuilder = new DongleFactsRequestBuilder();
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

    public DongleFactsRequestBuilder dongleFactsRequestBuilder() {
        return this.dongleFactsRequestBuilder;
    }
}
