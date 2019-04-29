package com.rposcro.jwavez.serial.frames

import com.rposcro.jwavez.serial.buffers.ViewBuffer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.TestUtils.asByteArray

class InboundFrameValidatorSpec extends Specification {

    @Shared
    def validator;

    def setupSpec() {
        validator = new InboundFrameValidator();
    }

    @Unroll
    def "passes validation"() {
        given:
        def buffer = new ViewBuffer(ByteBuffer.wrap(asByteArray(data)));

        when:
        buffer.setViewRange(0, data.size());

        then:
        validator.validateFrameLength(buffer);
        validator.validateFrameCategory(buffer);
        validator.validatePayloadSize(buffer);
        validator.validateFrameCRC(buffer);

        where:
        data | _
        [ 0x01, 0x03, 0x00, 0x44, 0xb8 ] | _
        [ 0x01, 0x05, 0x00, 0x55, 0x44, 0x33, 0xd8 ] | _
    }

    @Unroll
    def "fails frame CRC"() {
        given:
        def buffer = new ViewBuffer(ByteBuffer.wrap(asByteArray(data)));

        when:
        buffer.setViewRange(0, data.size());

        then:
        !validator.validateFrameCRC(buffer);

        where:
        data | _
        [ 0x01, 0x03, 0x00, 0x44, 0xaa ] | _
        [ 0x01, 0x05, 0x00, 0x55, 0x44, 0x33, 0x89 ] | _
    }

    @Unroll
    def "fails frame category"() {
        given:
        def buffer = new ViewBuffer(ByteBuffer.wrap(asByteArray(data)));

        when:
        buffer.setViewRange(0, data.size());

        then:
        !validator.validateFrameCategory(buffer);

        where:
        data | _
        [ 0x06, 0x03, 0x00, 0x44, 0xb8 ] | _
        [ 0x18, 0x05, 0x00, 0x55, 0x44, 0x33, 0xd8 ] | _
    }

    @Unroll
    def "fails payload size"() {
        given:
        def buffer = new ViewBuffer(ByteBuffer.wrap(asByteArray(data)));

        when:
        buffer.setViewRange(0, data.size());

        then:
        !validator.validatePayloadSize(buffer);

        where:
        data | _
        [ 0x01, 0x06, 0x00, 0x44, 0xb8 ] | _
        [ 0x01, 0x12, 0x00, 0x55, 0x44, 0x33, 0xd8 ] | _
    }

    @Unroll
    def "fails frame length"() {
        given:
        def buffer = new ViewBuffer(ByteBuffer.wrap(asByteArray(data)));

        when:
        buffer.setViewRange(0, data.size());

        then:
        !validator.validateFrameLength(buffer);

        where:
        data | _
        [ ] | _
        [ 0x01, 0x08 ] | _
    }
}
