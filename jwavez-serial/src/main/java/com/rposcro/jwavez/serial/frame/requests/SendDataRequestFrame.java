package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.TransmitOption;
import com.rposcro.jwavez.serial.utils.FrameDataBuilder;

@RequestFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataRequestFrame extends SOFRequestFrame {

  public SendDataRequestFrame(NodeId nodeId, byte callbackFunctionId, ZWaveControlledCommand zWaveCommand) {
    super(new FrameDataBuilder(9 + zWaveCommand.getPayloadLength())
        .frameType(FrameType.REQ)
        .serialCommand(SerialCommand.SEND_DATA)
        .withByte(nodeId.getId())
        .withByte((byte) zWaveCommand.getPayloadLength())
        .withBytes(zWaveCommand.getPayloadBuffer().cloneBytes())
        .withByte(defaultTransmitOptions())
        .withByte(callbackFunctionId)
        .buildData());
  }

  private static byte defaultTransmitOptions() {
    return TransmitOption.optionsBuilder()
        .withOption(TransmitOption.TRANSMIT_OPTION_ACK)
        .withOption(TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE)
        .build();
  }
}
