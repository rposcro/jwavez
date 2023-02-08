package com.rposcro.jwavez.serial.rxtx;

public interface SerialFrameConstants {

    byte CATEGORY_SOF = 0x01;
    byte CATEGORY_ACK = 0x06;
    byte CATEGORY_NAK = 0x15;
    byte CATEGORY_CAN = 0x18;

    byte TYPE_REQ = 0x00;
    byte TYPE_RES = 0x01;

    int MAX_Z_WAVE_FRAME_SIZE = 256;

    int FRAME_OFFSET_CATEGORY = 0;
    int FRAME_OFFSET_LENGTH = 1;
    int FRAME_OFFSET_TYPE = 2;
    int FRAME_OFFSET_COMMAND = 3;
    int FRAME_OFFSET_PAYLOAD = 4;
}
