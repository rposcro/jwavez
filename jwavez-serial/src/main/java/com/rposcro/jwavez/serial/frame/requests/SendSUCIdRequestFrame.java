package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.builders.TransmitOptionsBuilder;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.constants.TransmitOption;

@RequestFrameModel(function = SerialCommand.SEND_SUC_ID)
public class SendSUCIdRequestFrame extends SOFRequestFrame {

  public SendSUCIdRequestFrame(NodeId addresseeId) {
    super(SendSUCIdRequestFrame.class.getAnnotation(RequestFrameModel.class).function(), addresseeId.getId(), defaultTransmitOptions());
  }

  private static byte defaultTransmitOptions() {
    return new TransmitOptionsBuilder()
        .withOption(TransmitOption.TRANSMIT_OPTION_ACK)
        .withOption(TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE)
        .buildOptions();
  }
}
