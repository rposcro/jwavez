package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.zstick.ZStickConfigParameter;

@RequestFrameModel(function = SerialCommand.ZSTICK_SET_CONFIG)
public class ZStickSetConfigRequestFrame extends SOFRequestFrame {

  private ZStickSetConfigRequestFrame(int dataLength, byte... buffer) {
    super(SerialCommand.ZSTICK_SET_CONFIG, dataLength, buffer);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private byte[] payload = new byte[255];
    private int idx = 0;

    public Builder ledIndicator(boolean isOn) {
      addUnoParam(ZStickConfigParameter.USB_LED_INDICATOR, (byte) (isOn ? 1 : 0));
      return this;
    }

    public Builder rfPowerLevel(int level) {
      addUnoParam(ZStickConfigParameter.RF_POWER_LEVEL, (byte) (Math.min(level, 10)));
      return this;
    }

    public ZStickSetConfigRequestFrame build() {
      return new ZStickSetConfigRequestFrame(idx, payload);
    }

    private void addUnoParam(ZStickConfigParameter param, byte value) {
      payload[idx++] = param.getParameterCode();
      payload[idx++] = 1;
      payload[idx++] = value;
    }
  }
}
