package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.builders.TransmitOptionsBuilder;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.TransmitOption;
import com.rposcro.jwavez.serial.builders.FrameDataBuilder;

@RequestFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataRequestFrame extends SOFRequestFrame {

  public SendDataRequestFrame(NodeId addresseeId, ZWaveControlledCommand zWaveCommand, byte callbackFunctionId) {
    super(new FrameDataBuilder(9 + zWaveCommand.getPayloadLength())
        .frameType(FrameType.REQ)
        .serialCommand(SerialCommand.SEND_DATA)
        .withByte(addresseeId.getId())
        .withByte((byte) zWaveCommand.getPayloadLength())
        .withBytes(zWaveCommand.getPayloadBuffer().cloneBytes())
        .withByte(defaultTransmitOptions())
        .withByte(callbackFunctionId)
        .buildData());
  }

  private static byte defaultTransmitOptions() {
    return new TransmitOptionsBuilder()
        .withOption(TransmitOption.TRANSMIT_OPTION_ACK)
        .withOption(TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE)
        .buildOptions();
  }
}
