package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;

abstract class AbstractFlowHandler {

  abstract void startOver(byte callbackFlowId);
  abstract void stopTransaction();
  abstract void killTransaction();
  abstract NodeInfo getNodeInfo();
  abstract void handleCallback(ZWaveCallback zWaveCallback);
}
