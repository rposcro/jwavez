package com.rposcro.jwavez.serial.utils

import com.rposcro.jwavez.serial.buffers.ViewBuffer
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.TestUtils.asByteArray

class FrameUtilSpec extends Specification {

    @Unroll
    def "CRC is properly calculated based on byte buffer"() {
        given:
        def array = asByteArray(data);
        def buffer = ByteBuffer.wrap(array);

        when:
        buffer.position(3);
        int crc = FrameUtil.frameCRC(buffer) & 0xff;

        then:
        crc == expectedCrc;

        where:
        data | expectedCrc
        [ 0x01, 0x03, 0x00, 0x15, 0x00 ] | 0xe9
        [ 0x01, 0x10, 0x01, 0x15, 0x5a, 0x2d, 0x57, 0x61, 0x76, 0x65, 0x20, 0x33, 0x2e, 0x39, 0x35, 0x00, 0x01, 0x99 ] | 0x99
    }

    @Unroll
    def "CRC is properly calculated based on view buffer"() {
        given:
        def array = asByteArray(data);
        def buffer = new ViewBuffer(ByteBuffer.wrap(array));

        when:
        buffer.setViewRange(0, array.length);
        buffer.position(3);
        int crc = FrameUtil.frameCRC(buffer) & 0xff;

        then:
        crc == expectedCrc;

        where:
        data | expectedCrc
        [ 0x01, 0x03, 0x00, 0x15, 0x00 ] | 0xe9
        [ 0x01, 0x10, 0x01, 0x15, 0x5a, 0x2d, 0x57, 0x61, 0x76, 0x65, 0x20, 0x33, 0x2e, 0x39, 0x35, 0x00, 0x01, 0x99 ] | 0x99
    }

    @Unroll
    def "CRC is properly calculated based on byte array"() {
        given:
        def buffer = asByteArray(data);

        when:
        int crc = FrameUtil.frameCRC(buffer) & 0xff;

        then:
        crc == expectedCrc;

        where:
        data | expectedCrc
        [ 0x01, 0x03, 0x00, 0x15, 0x00 ] | 0xe9
        [ 0x01, 0x10, 0x01, 0x15, 0x5a, 0x2d, 0x57, 0x61, 0x76, 0x65, 0x20, 0x33, 0x2e, 0x39, 0x35, 0x00, 0x01, 0x99 ] | 0x99
    }
}
