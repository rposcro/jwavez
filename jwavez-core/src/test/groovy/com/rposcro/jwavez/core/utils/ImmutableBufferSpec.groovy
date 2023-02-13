package com.rposcro.jwavez.core.utils

import com.rposcro.jwavez.core.buffer.ImmutableBuffer
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
        data   | expected
        [0x00] | 0
        [0x60] | 96
        [0x7f] | 127
        [0x80] | -128
        [0xFF] | -1
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
        data   | expected
        [0x00] | 0
        [0x60] | 96
        [0x7f] | 127
        [0x80] | 128
        [0xFF] | 255
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
        data         | expected
        [0x00, 0x00] | 0
        [0x00, 0x7f] | 127
        [0x00, 0x80] | 128
        [0x00, 0xff] | 255
        [0x01, 0x00] | 256
        [0x01, 0xff] | 511
        [0x02, 0x04] | 516
        [0xff, 0xff] | -1
        [0x7f, 0xff] | Short.MAX_VALUE
        [0x80, 0x00] | Short.MIN_VALUE
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
        data         | expected
        [0x00, 0x00] | 0
        [0x00, 0x7f] | 127
        [0x00, 0x80] | 128
        [0x00, 0xff] | 255
        [0x01, 0x00] | 256
        [0x01, 0xff] | 511
        [0x02, 0x04] | 516
        [0xff, 0xff] | 65535
        [0x7f, 0xff] | Short.MAX_VALUE
        [0x80, 0x00] | Short.MAX_VALUE + 1
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
        data                     | expected
        [0x00, 0x00, 0x00, 0x00] | 0
        [0x00, 0x00, 0x00, 0xff] | 255
        [0x00, 0x00, 0x01, 0xff] | 511
        [0x00, 0x00, 0xff, 0xff] | 65535
        [0x00, 0x01, 0x00, 0x00] | 65536
        [0xff, 0xff, 0xff, 0xff] | -1
        [0x80, 0x00, 0x00, 0x00] | Integer.MIN_VALUE
        [0x7f, 0xff, 0xff, 0xff] | Integer.MAX_VALUE
    }

    def "next byte returned #expected times"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());
        def seqReceived = new byte[expected];

        when:
        for (int i = 0; i < expected; i++) {
            seqReceived[i] = buffer.nextByte();
        }

        then:
        buffer.available() == 0
        seqReceived == data

        where:
        data               | expected
        []                 | 0
        [0x00]             | 1
        [0x00, 0x01]       | 2
        [0x00, 0x01, 0x02] | 3
    }

    def "next word returned #count times"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());
        def seqReceived = new short[count];

        when:
        for (int i = 0; i < count; i++) {
            seqReceived[i] = buffer.nextWord();
        }

        then:
        buffer.available() == 0
        seqReceived == words

        where:
        data                                 | words                 | count
        []                                   | []                    | 0
        [0x01, 0x01]                         | [0x0101]              | 1
        [0x01, 0x05, 0x82, 0x02]             | [261, -32254]         | 2
        [0x89, 0x04, 0x02, 0x06, 0x80, 0x01] | [-30460, 518, -32767] | 3
    }

    def "next double word returned #count times"() {
        given:
        def dataArray = data as byte[];
        def buffer = new ImmutableBuffer(dataArray, 0, dataArray.size());
        def seqReceived = new int[count];

        when:
        for (int i = 0; i < count; i++) {
            seqReceived[i] = buffer.nextDoubleWord();
        }

        then:
        buffer.available() == 0
        seqReceived == words

        where:
        data                     | words      | count
        []                       | []         | 0
        [0x01, 0x05, 0x82, 0x02] | [17138178] | 1
    }
}
