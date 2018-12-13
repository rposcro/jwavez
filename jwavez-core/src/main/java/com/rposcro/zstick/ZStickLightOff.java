package com.rposcro.zstick;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;

public class ZStickLightOff {

  public static void main(String[] args) throws Exception {
    SerialManager manager = new SerialManager("/dev/cu.usbmodem1421");
    SerialChannel channel = manager.connect();

//    channel.sendFrame(ZStickSetConfigRequestFrame.builder()
//        .ledIndicator(false)
//        .build()
//    ).get();
  }

}
