package com.rposcro.jwavez.serial.frames

import com.rposcro.jwavez.serial.TestUtils
import com.rposcro.jwavez.serial.buffers.ViewBuffer
import com.rposcro.jwavez.serial.frames.callbacks.SendSUCIdCallback
import com.rposcro.jwavez.serial.frames.callbacks.SetLearnModeCallback
import com.rposcro.jwavez.serial.frames.callbacks.UnknownCallback
import com.rposcro.jwavez.serial.frames.responses.EnableSUCResponse
import com.rposcro.jwavez.serial.frames.responses.GetLibraryTypeResponse
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse
import com.rposcro.jwavez.serial.frames.responses.UnknownResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

class InboundFrameParserSpec extends Specification {

    @Shared
    def parser;

    def setupSpec() {
        parser = InboundFrameParser.builder().frameRegistry(FramesModelRegistry.defaultRegistry()).build();
    }

    @Unroll
    def "parses known responses"() {
        given:
        def buffer = viewBuffer(data);

        when:
        def response = parser.parseResponseFrame(buffer);

        then:
        response.getClass() == expectedClass;

        where:
        data | expectedClass
        [ 0x01, 0x03, 0x01, 0x52, 0xaf ] | EnableSUCResponse.class
        [ 0x01, 0x04, 0x01, 0xbd, 0x03, 0x44 ] | GetLibraryTypeResponse.class
        [ 0x01, 0x08, 0x01, 0x20, 0xcc, 0xdd, 0xee, 0xff, 0x44, 0x92 ] | MemoryGetIdResponse.class
    }

    @Unroll
    def "parses unknown responses"() {
        given:
        def buffer = viewBuffer(data);

        when:
        def response = parser.parseResponseFrame(buffer);

        then:
        response.getClass() == UnknownResponse.class;

        where:
        data | _
        [ 0x01, 0x03, 0x01, 0xb8, 0x45 ] | _
    }

    @Unroll
    def "parses known callbacks"() {
        given:
        def buffer = viewBuffer(data);

        when:
        def callback = parser.parseCallbackFrame(buffer);

        then:
        callback.getClass() == expectedClass;

        where:
        data | expectedClass
        [ 0x01, 0x05, 0x00, 0x57, 0x11, 0x00, 0xaf ] | SendSUCIdCallback.class
        [ 0x01, 0x05, 0x00, 0x50, 0x11, 0x06, 0x22, 0xaf ] | SetLearnModeCallback.class
    }

    @Unroll
    def "parses unknown callbacks"() {
        given:
        def buffer = viewBuffer(data);

        when:
        def callback = parser.parseCallbackFrame(buffer);

        then:
        callback.getClass() == UnknownCallback.class;

        where:
        data | _
        [ 0x01, 0x03, 0x00, 0xb8, 0x45 ] | _
    }

    def viewBuffer(List<Integer> data) {
        ViewBuffer buffer = new ViewBuffer(ByteBuffer.wrap(TestUtils.asByteArray(data)));
        buffer.setViewRange(0, data.size());
        return buffer;
    }
}
