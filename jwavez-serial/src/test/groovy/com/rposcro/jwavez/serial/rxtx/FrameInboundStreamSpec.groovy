package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.StreamException
import com.rposcro.jwavez.serial.rxtx.MockedSerialPort
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.TestUtils.dataFromBuffer
import static SerialFrameConstants.CATEGORY_ACK
import static SerialFrameConstants.CATEGORY_CAN
import static SerialFrameConstants.CATEGORY_NAK
import static SerialFrameConstants.CATEGORY_SOF
import static java.lang.Byte.toUnsignedInt

class FrameInboundStreamSpec extends Specification {

    static final ACK = toUnsignedInt(CATEGORY_ACK);
    static final NAK = toUnsignedInt(CATEGORY_NAK);
    static final CAN = toUnsignedInt(CATEGORY_CAN);
    static final SOF = toUnsignedInt(CATEGORY_SOF);

    def rxTxConfiguration;
    def serialPort;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        serialPort = new MockedSerialPort();
    }

    def "no data in pipe"() {
        given:
        def inboundStream = makeStream([]);

        when:
        def buffer = inboundStream.nextFrame();

        then:
        !buffer.hasRemaining();
    }

    @Unroll
    def "single correct frame in pipe #inboundData"() {
        given:
        def inboundStream = makeStream([inboundData]);

        when:
        def buffer = inboundStream.nextFrame();

        then:
        buffer.remaining() == inboundData.size();
        dataFromBuffer(buffer) == inboundData;

        where:
        inboundData                   | _
        [SOF, 0x03, 0x00, 0x06, 0xaa] | _
        [ACK]                         | _
        [NAK]                         | _
        [CAN]                         | _
    }

    def "single odd frame in pipe"() {
        given:
        def inboundStream = makeStream([[0x80]]);

        when:
        inboundStream.nextFrame();

        then:
        thrown StreamException;
    }

    @Unroll
    def "refills buffer correctly #inboundData"() {
        given:
        def inboundStream = makeStream([inboundData]);

        when:
        inboundStream.refillBuffer(3);
        def lim1 = inboundStream.frameBuffer.limit();
        inboundStream.refillBuffer(4);
        def lim2 = inboundStream.frameBuffer.limit();

        then:
        inboundStream.frameBuffer.position() == 0;
        lim1 == expLim1;
        lim2 == expLim2;

        where:
        inboundData                                      | expLim1 | expLim2
        [0x01, 0x06, 0x00, 0x06, 0x34, 0x11, 0x67, 0xbb] | 3       | 7
        [0x01, 0x06, 0x00, 0x06, 0x34, 0x11, 0x67]       | 3       | 7
    }

    @Unroll
    def "refills times out #inboundData"() {
        given:
        rxTxConfiguration.frameCompleteTimeout = 10;
        def inboundStream = makeStream([inboundData]);

        when:
        inboundStream.refillBuffer(3);
        inboundStream.refillBuffer(4);

        then:
        thrown StreamException;

        where:
        inboundData                    | _
        [0x01, 0x06, 0x00, 0x06, 0x34] | _
        [0x01, 0x06, 0x00]             | _
        [0x01, 0x06]                   | _
    }

    @Unroll
    def "chunked sof frame leading in pipe #inboundData"() {
        given:
        def inboundStream = makeStream(inboundData);
        def allDataFlat = inboundData.flatten();
        def expectedData = allDataFlat.subList(0, allDataFlat.get(1) + 2);

        when:
        def buffer = inboundStream.nextFrame();

        then:
        inboundStream.frameBuffer.position() == expPos;
        inboundStream.frameBuffer.limit() == expLim;
        buffer.remaining() == expectedData.size();
        dataFromBuffer(buffer) == expectedData;

        where:
        inboundData                                                          | expPos | expLim
        [[0x01, 0x06, 0x00, 0x06, 0x34], [0x11, 0x67, 0xbb]]                 | 8      | 8
        [[0x01], [0x06, 0x00, 0x06, 0x34, 0x11, 0x67, 0xbb]]                 | 8      | 8
        [[0x01], [0x06, 0x00, 0x06, 0x34], [0x11, 0x67, 0xbb]]               | 8      | 8
        [[0x01], [0x03, 0x00, 0x06], [0xbb], [0x06, 0x15]]                   | 5      | 5
        [[0x01], [0x03, 0x00, 0x06], [0xbb], [0x01, 0x03, 0x01, 0x06, 0xcc]] | 5      | 5
        [[0x01], [0x03, 0x00, 0x06], [0xbb, 0x01, 0x03, 0x01, 0x06, 0xcc]]   | 5      | 5
        [[0x01], [0x03, 0x00, 0x06, 0xbb, 0x01, 0x03, 0x01, 0x06, 0xcc]]     | 5      | 5
        [[0x01, 0x04, 0x00, 0x06, 0x01, 0xbb, 0x01, 0x03, 0x01, 0x06, 0xcc]] | 6      | 11
    }

    @Unroll
    def "multiple frames in pipe #streamData"() {
        given:
        def inboundStream = makeStream(inboundData);

        when:
        def counter = 0;
        while (inboundStream.nextFrame().hasRemaining()) {
            counter++;
        }

        then:
        counter == framesCnt;

        where:
        inboundData                                                                        | framesCnt
        [[]]                                                                               | 0
        [[0x15]]                                                                           | 1
        [[0x06, 0x15, 0x18]]                                                               | 3
        [[0x06], [0x15, 0x18]]                                                             | 3
        [[0x06], [0x15, 0x18, 0x01], [0x05], [0x01, 0x21, 0x22, 0x18, 0xff, 0x18], [0x06]] | 6
    }

    @Unroll
    def "successfully purges stream #inboundData"() {
        given:
        def inboundStream = makeStream(inboundData);

        when:
        inboundStream.nextFrame();
        inboundStream.purgeStream();

        then:
        inboundStream.frameBuffer.position() == 0;
        inboundStream.frameBuffer.limit() == 0;
        !serialPort.inboundDataAvailable();

        where:
        inboundData                                                                        | _
        [[]]                                                                               | _
        [[0x15]]                                                                           | _
        [[0x06, 0x15, 0x18]]                                                               | _
        [[0x06], [0x15, 0x18]]                                                             | _
        [[0x06], [0x15, 0x18, 0x01], [0x05], [0x01, 0x21, 0x22, 0x18, 0xff, 0x18], [0x06]] | _
    }


    @Unroll
    def "check mocked port #inboundData"() {
        given:
        def port = new MockedSerialPort(inboundData).reset();
        def buffer = ByteBuffer.allocateDirect(10);

        when:
        buffer.position(0).limit(bufferLimit);
        port.readData(buffer);

        then:
        buffer.position() == Math.min(bufferLimit, inboundData.size());

        where:
        inboundData                    | bufferLimit
        [0x01, 0x03, 0x00, 0x06, 0xaa] | 3
        [0x01, 0x03, 0x00, 0x06, 0xaa] | 6
        [0x06]                         | 3
    }

    def makeStream(List<List<Integer>> inboundData) {
        inboundData.forEach({ series -> serialPort.addSeries(series) });
        serialPort.reset();
        return FrameInboundStream.builder()
                .serialPort(serialPort)
                .configuration(rxTxConfiguration)
                .build();
    }
}
