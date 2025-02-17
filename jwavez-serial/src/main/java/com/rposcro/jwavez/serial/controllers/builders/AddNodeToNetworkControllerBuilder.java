package com.rposcro.jwavez.serial.controllers.builders;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkController;
import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowHandler;
import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState;
import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequestBuilder;
import lombok.Getter;

@Getter
public class AddNodeToNetworkControllerBuilder extends AbstractInclusionControllerBuilder<AddNodeToNetworkFlowState, AddNodeToNetworkControllerBuilder> {

    public AddNodeToNetworkController build() {
        fillDefaults();
        assureReadiness();
        return new AddNodeToNetworkController(this);
    }

    protected void assureReadiness() {
        TransactionKeeper transactionKeeper = new TransactionKeeper();
        AddNodeToNetworkRequestBuilder requestBuilder = JwzSerialSupport.defaultSupport().serialRequestFactory().addNodeToNetworkRequestsBuilder();
        AddNodeToNetworkFlowHandler flowHandler = new AddNodeToNetworkFlowHandler(transactionKeeper, requestBuilder);

        super.assureReadiness(transactionKeeper, flowHandler);
    }
}
