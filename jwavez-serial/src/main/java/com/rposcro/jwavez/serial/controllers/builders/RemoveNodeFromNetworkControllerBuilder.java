package com.rposcro.jwavez.serial.controllers.builders;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkController;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowHandler;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState;
import com.rposcro.jwavez.serial.frames.requests.RemoveNodeFromNetworkRequestBuilder;
import lombok.Getter;

@Getter
public class RemoveNodeFromNetworkControllerBuilder extends AbstractInclusionControllerBuilder<RemoveNodeFromNetworkFlowState, RemoveNodeFromNetworkControllerBuilder> {

    public RemoveNodeFromNetworkController build() {
        fillDefaults();
        assureReadiness();
        return new RemoveNodeFromNetworkController(this);
    }

    protected void assureReadiness() {
        TransactionKeeper transactionKeeper = new TransactionKeeper();
        RemoveNodeFromNetworkRequestBuilder requestBuilder = JwzSerialSupport.defaultSupport().serialRequestFactory().removeNodeFromNetworkRequestBuilder();
        RemoveNodeFromNetworkFlowHandler flowHandler = new RemoveNodeFromNetworkFlowHandler(transactionKeeper, requestBuilder);

        super.assureReadiness(transactionKeeper, flowHandler);
    }
}
