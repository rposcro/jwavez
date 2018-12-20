package com.rposcro.jwavez.core.utils

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ImmutableBufferSpec extends Specification {

    def "signed byte should be #expected"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());

        when:
        def value = buffer.getByte(0);

        then:
        value == expected;

        where:
        data     | expected
        [0x00]   | 0
        [0x60]   | 96
        [0x7f]   | 127
        [0x80]   | -128
        [0xFF]   | -1
    }

    def "unsigned byte should be #expected"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());

        when:
        def value = buffer.getUnsignedByte(0);

        then:
        value == expected;

        where:
        data     | expected
        [0x00]   | 0
        [0x60]   | 96
        [0x7f]   | 127
        [0x80]   | 128
        [0xFF]   | 255
    }

    def "signed word should be #expected"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());

        when:
        def value = buffer.getWord(0);

        then:
        value == expected;

        where:
        data            | expected
        [0x00, 0x00 ]   | 0
        [0x00, 0x7f ]   | 127
        [0x00, 0x80 ]   | 128
        [0x00, 0xff ]   | 255
        [0x01, 0x00 ]   | 256
        [0x01, 0xff ]   | 511
        [0x02, 0x04 ]   | 516
        [0xff, 0xff ]   | -1
        [0x7f, 0xff ]   | Short.MAX_VALUE
        [0x80, 0x00 ]   | Short.MIN_VALUE
    }

    def "unsigned word should be #expected"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());

        when:
        def value = buffer.getUnsignedWord(0);

        then:
        value == expected;

        where:
        data            | expected
        [0x00, 0x00 ]   | 0
        [0x00, 0x7f ]   | 127
        [0x00, 0x80 ]   | 128
        [0x00, 0xff ]   | 255
        [0x01, 0x00 ]   | 256
        [0x01, 0xff ]   | 511
        [0x02, 0x04 ]   | 516
        [0xff, 0xff ]   | 65535
        [0x7f, 0xff ]   | Short.MAX_VALUE
        [0x80, 0x00 ]   | Short.MAX_VALUE + 1
    }

    def "signed double word should be #expected"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());

        when:
        def value = buffer.getDoubleWord(0);

        then:
        value == expected;

        where:
        data                        | expected
        [0x00, 0x00, 0x00, 0x00]    | 0
        [0x00, 0x00, 0x00, 0xff]    | 255
        [0x00, 0x00, 0x01, 0xff]    | 511
        [0x00, 0x00, 0xff, 0xff]    | 65535
        [0x00, 0x01, 0x00, 0x00]    | 65536
        [0xff, 0xff, 0xff, 0xff]    | -1
        [0x80, 0x00, 0x00, 0x00]    | Integer.MIN_VALUE
        [0x7f, 0xff, 0xff, 0xff]    | Integer.MAX_VALUE
    }
}
