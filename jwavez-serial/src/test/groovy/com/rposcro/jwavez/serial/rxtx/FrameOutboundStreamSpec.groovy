package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.rxtx.MockedSerialPort;
import spock.lang.Specification

import java.nio.ByteBuffer;

public class FrameOutboundStreamSpec extends Specification {

    def rxTxConfiguration;
    def port;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        port = new MockedSerialPort();
    }

    def "writes ack"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialPort(port).build();

        when:
        outboundStream.writeACK();

        then:
        port.outboundData.size() == 1;
        port.outboundData.get(0) == 0x06;
    }

    def "writes nak"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialPort(port).build();

        when:
        outboundStream.writeNAK();

        then:
        port.outboundData.size() == 1;
        port.outboundData.get(0) == 0x15;
    }

    def "writes can"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialPort(port).build();

        when:
        outboundStream.writeCAN();

        then:
        port.outboundData.size() == 1;
        port.outboundData.get(0) == 0x18;
    }

    def "writes sof"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialPort(port).build();
        def data = [0x01, 0x03, 0x00, 0x59, 0xd8];
        def outBuffer = ByteBuffer.allocate(255);
        data.forEach({ val -> outBuffer.put((byte) val) });
        outBuffer.limit(outBuffer.position()).position(0);

        when:
        outboundStream.writeSOF(outBuffer);

        then:
        port.outboundData.size() == 0x05;
        port.outboundData == data;
    }
}