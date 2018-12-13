package com.rposcro.zstick;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;

public class ZStickTest {

  public static void main(String[] args) throws Exception {
    SerialManager manager = new SerialManager("/dev/cu.usbmodem1411");
    SerialChannel channel = manager.connect();

//    MemoryGetIdResponseFrame mgiFrame = (MemoryGetIdResponseFrame) channel.sendFrame(new MemoryGetIdRequestFrame()).get();
//    System.out.println(String.format("HomeId: %h, nodeId: %s", mgiFrame.getHomeId(), mgiFrame.getNodeId()));

//    GetVersionResponseFrame gvFrame = (GetVersionResponseFrame) channel.sendFrame(new GetVersionRequestFrame()).get();
//    System.out.println(String.format("Version: %s, responseData: %s", gvFrame.getVersion(), gvFrame.getResponseData()));

//    RequestNodeInfoResponseFrame rniFrame = (RequestNodeInfoResponseFrame) channel.sendFrame(new RequestNodeInfoRequestFrame((byte) 1)).get();
//    System.out.println(String.format("Request node info: %s", rniFrame.isSuccessful()));

//    GetSUCNodeIdResponseFrame gsucniFrame = (GetSUCNodeIdResponseFrame) channel.sendFrame(new GetSUCNodeIdRequestFrame()).get();
//    System.out.println(String.format("SUC Node Id: %s", gsucniFrame.getSucNodeId()));

//    EnableSUCResponseFrame esucFrame = (EnableSUCResponseFrame) channel.sendFrame(new EnableSUCRequestFrame()).get();
//    System.out.println(String.format("SUC Enabled:"));

//    SetSUCNodeIdResponseFrame ssucnFramw = (SetSUCNodeIdResponseFrame) channel.sendFrame(new SetSUCNodeIdRequestFrame((byte) 1, true, true)).get();
//    System.out.println(String.format("Set SUC success: %s", ssucnFramw.isStatus()));
//
//    gsucniFrame = (GetSUCNodeIdResponseFrame) channel.sendFrame(new GetSUCNodeIdRequestFrame()).get();
//    System.out.println(String.format("SUC Node Id: %s", gsucniFrame.getSucNodeId()));




//    GetVersionResponseFrame frame = (GetVersionResponseFrame) serialChannel.sendFrameWithResponse(new GetVersionRequestFrame());
//    System.out.println(String.format("Version: %s, responseData: %s", frame.getVersion(), frame.getResponseData()));
//
//    MemoryGetIdResponseFrame fframe = (MemoryGetIdResponseFrame) serialChannel.sendFrameWithResponse(new MemoryGetIdRequestFrame());
//    System.out.println(String.format("HomeId: %h, nodeId: %s", fframe.getHomeId(), fframe.getNodeId()));
//
//    serialChannel.sendFrameWithResponse(
//        ZStickSetConfigRequestFrame.builder()
//        .ledIndicator(true)
//        .build()
//    );
//
//    ZStickGetConfigResponseFrame cframe = (ZStickGetConfigResponseFrame) serialChannel.sendFrameWithResponse(
//      new ZStickGetConfigRequestFrame(ZStickConfigParameter.RF_POWER_LEVEL, ZStickConfigParameter.USB_LED_INDICATOR)
//    );
//    System.out.println(String.format("PowerLevel: %s, LedIndicator: %s",
//        orNull(cframe.getParameterValue(ZStickConfigParameter.RF_POWER_LEVEL)),
//        orNull(cframe.getParameterValue(ZStickConfigParameter.USB_LED_INDICATOR))
//        ));

    System.out.println("Channel obtained");
    //Thread.sleep(3000);
    //System.exit(0);
  }

  private static String orNull(byte[] array) {
    return array == null ? "null" : "" + array[0];
  }
}
