package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFlowHandler {

    abstract void startOver(byte callbackFlowId);

    abstract void stopTransaction();

    abstract void killTransaction();

    abstract NodeId getNodeId();

    public abstract void handleCallback(ZWaveCallback zWaveCallback);
}
