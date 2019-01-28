package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.rxtz.MockedSerialConnection;
import spock.lang.Specification

import java.nio.ByteBuffer;

public class FrameOutboundStreamSpec extends Specification {

    def rxTxConfiguration;
    def connection;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        connection = new MockedSerialConnection();
    }

    def "writes ack"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialConnection(connection).build();

        when:
        outboundStream.writeACK();

        then:
        connection.outboundData.size() == 1;
        connection.outboundData.get(0) == 0x06;
    }

    def "writes nak"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialConnection(connection).build();

        when:
        outboundStream.writeNAK();

        then:
        connection.outboundData.size() == 1;
        connection.outboundData.get(0) == 0x15;
    }

    def "writes can"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialConnection(connection).build();

        when:
        outboundStream.writeCAN();

        then:
        connection.outboundData.size() == 1;
        connection.outboundData.get(0) == 0x18;
    }

    def "writes sof"() {
        given:
        def outboundStream = FrameOutboundStream.builder().serialConnection(connection).build();
        def data = [0x01, 0x03, 0x00, 0x59, 0xd8];
        def outBuffer = ByteBuffer.allocate(255);
        data.forEach({val -> outBuffer.put((byte) val)});
        outBuffer.limit(outBuffer.position()).position(0);

        when:
        outboundStream.writeSOF(outBuffer);

        then:
        connection.outboundData.size() == 0x05;
        connection.outboundData == data;
    }
}