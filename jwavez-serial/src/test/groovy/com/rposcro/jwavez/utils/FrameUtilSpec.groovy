package com.rposcro.jwavez.utils

import spock.lang.Specification

class FrameUtilSpec extends Specification {

    def "CRC is properly calculated"() {
        given:
        byte[] buffer1 = [ 0x01, 0x03, 0x00, 0x15, 0x00 ];
        byte[] buffer2 = [ 0x01, 0x10, 0x01, 0x15, 0x5a, 0x2d, 0x57, 0x61, 0x76, 0x65, 0x20, 0x33, 0x2e, 0x39, 0x35, 0x00, 0x01, 0x99 ];

        when:
        int crc1 = FrameUtil.frameCRC(buffer1) & 0xff;
        int crc2 = FrameUtil.frameCRC(buffer2) & 0xff;

        then:
        crc1 == 0xe9;
        crc2 == 0x99;
    }
}
