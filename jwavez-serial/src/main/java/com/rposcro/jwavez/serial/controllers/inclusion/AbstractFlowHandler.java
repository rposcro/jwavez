package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;

abstract class AbstractFlowHandler {

    abstract void startOver(byte callbackFlowId);

    abstract void stopTransaction();

    abstract void killTransaction();

    abstract NodeId getNodeId();

    abstract void handleCallback(ZWaveCallback zWaveCallback);
}
