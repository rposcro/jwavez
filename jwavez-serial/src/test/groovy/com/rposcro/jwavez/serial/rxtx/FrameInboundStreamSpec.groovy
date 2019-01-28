package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.SerialStreamException
import com.rposcro.jwavez.serial.rxtz.MockedSerialConnection
import com.rposcro.jwavez.serial.utils.ViewBuffer
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

import java.nio.ByteBuffer
import java.util.stream.Collectors

class FrameInboundStreamSpec extends Specification {

    def rxTxConfiguration;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
    }

    def "no data in pipe"() {
        given:
        def connection = new MockedSerialConnection(Collections.emptyList()).reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        def buffer = inboundStream.nextFrame();

        then:
        !buffer.hasRemaining();
    }

    @Unroll
    def "single correct frame in pipe #streamData"() {
        given:
        def connection = new MockedSerialConnection(streamData).reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        def buffer = inboundStream.nextFrame();

        then:
        buffer.remaining() == streamData.size();
        dataSeriesFromBuffer(buffer) == streamData;

        where:
        streamData | _
        [0x01, 0x03, 0x00, 0x06, 0xaa] | _
        [0x06] | _
        [0x15] | _
        [0x18] | _
    }

    def "single odd frame in pipe"() {
        given:
        def connection = new MockedSerialConnection(Collections.singletonList(0x80)).reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        def buffer = inboundStream.nextFrame();

        then:
        thrown SerialStreamException;
    }

    @Unroll
    def "refills buffer correctly #streamData"() {
        given:
        def connection = new MockedSerialConnection(streamData).reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        inboundStream.refillBuffer(3);
        def pos1 = inboundStream.frameBuffer.position();
        def lim1 = inboundStream.frameBuffer.limit();

        inboundStream.refillBuffer(4);
        def pos2 = inboundStream.frameBuffer.position();
        def lim2 = inboundStream.frameBuffer.limit();

        then:
        pos1 == 0;
        pos2 == 0;
        lim1 == expLim1;
        lim2 == expLim2;

        where:
        streamData                                          | expLim1   | expLim2
        [0x01, 0x06, 0x00, 0x06, 0x34, 0x11, 0x67, 0xbb]    | 3         | 7
        [0x01, 0x06, 0x00, 0x06, 0x34, 0x11, 0x67]          | 3         | 7
    }

    @Unroll
    def "refills times out #streamData"() {
        given:
        def connection = new MockedSerialConnection(streamData).reset();
        rxTxConfiguration.frameCompleteTimeout = 50;
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        inboundStream.refillBuffer(3);
        inboundStream.refillBuffer(4);

        then:
        thrown SerialStreamException;

        where:
        streamData                                          | _
        [0x01, 0x06, 0x00, 0x06, 0x34]                      | _
        [0x01, 0x06, 0x00]                                  | _
        [0x01, 0x06]                                        | _
    }

    @Unroll
    def "chunked sof frame leading in pipe #streamData"() {
        given:
        def connection = new MockedSerialConnection();
        streamData.stream().forEach({ data -> connection.addSeries(data); });
        connection.reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();
        def allDataFlat = streamData.flatten();
        def expectedData = allDataFlat.subList(0, allDataFlat.get(1) + 2);

        when:
        def buffer = inboundStream.nextFrame();

        then:
        inboundStream.frameBuffer.position() == expPos;
        inboundStream.frameBuffer.limit() == expLim;
        buffer.remaining() == expectedData.size();
        dataSeriesFromBuffer(buffer) == expectedData;

        where:
        streamData                                                              | expPos | expLim
        [[0x01, 0x06, 0x00, 0x06, 0x34], [0x11, 0x67, 0xbb]]                    | 8      | 8
        [[0x01], [0x06, 0x00, 0x06, 0x34, 0x11, 0x67, 0xbb]]                    | 8      | 8
        [[0x01], [0x06, 0x00, 0x06, 0x34], [0x11, 0x67, 0xbb]]                  | 8      | 8
        [[0x01], [0x03, 0x00, 0x06], [0xbb], [0x06, 0x15]]                      | 5      | 5
        [[0x01], [0x03, 0x00, 0x06], [0xbb], [0x01, 0x03, 0x01, 0x06, 0xcc]]    | 5      | 5
        [[0x01], [0x03, 0x00, 0x06], [0xbb, 0x01, 0x03, 0x01, 0x06, 0xcc]]      | 5      | 5
        [[0x01], [0x03, 0x00, 0x06, 0xbb, 0x01, 0x03, 0x01, 0x06, 0xcc]]        | 5      | 5
        [[0x01, 0x04, 0x00, 0x06, 0x01, 0xbb, 0x01, 0x03, 0x01, 0x06, 0xcc]]    | 6      | 11
    }

    @Unroll
    def "multiple frames in pipe #streamData"() {
        given:
        def connection = new MockedSerialConnection();
        streamData.stream().forEach({ data -> connection.addSeries(data); });
        connection.reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        def counter = 0;
        while(inboundStream.nextFrame().hasRemaining()) {
            counter++;
        }

        then:
        counter == framesCnt;

        where:
        streamData | framesCnt
        [[]] | 0
        [[0x15]] | 1
        [[0x06, 0x15, 0x18]] | 3
        [[0x06], [0x15, 0x18]] | 3
        [[0x06], [0x15, 0x18, 0x01], [0x05], [0x01, 0x21, 0x22, 0x18, 0xff, 0x18], [0x06]] | 6
    }

    @Unroll
    def "successfully purges stream #streamData"() {
        given:
        def connection = new MockedSerialConnection();
        streamData.stream().forEach({ data -> connection.addSeries(data); });
        connection.reset();
        def inboundStream = FrameInboundStream.builder()
                .configuration(rxTxConfiguration)
                .serialConnection(connection)
                .build();

        when:
        inboundStream.nextFrame();
        inboundStream.purgeStream();

        then:
        inboundStream.frameBuffer.position() == 0;
        inboundStream.frameBuffer.limit() == 0;
        !connection.chunksIterator.hasNext();
        !connection.seriesIterator.hasNext();

        where:
        streamData | _
        [[]] | _
        [[0x15]] | _
        [[0x06, 0x15, 0x18]] | _
        [[0x06], [0x15, 0x18]] | _
        [[0x06], [0x15, 0x18, 0x01], [0x05], [0x01, 0x21, 0x22, 0x18, 0xff, 0x18], [0x06]] | _
    }


    @Unroll
    def "check mocked connection #streamData"() {
        given:
        def connection = new MockedSerialConnection(streamData).reset();
        def buffer = ByteBuffer.allocateDirect(10);

        when:
        buffer.position(0).limit(bufferLimit);
        connection.readData(buffer);

        then:
        buffer.position() == Math.min(bufferLimit, streamData.size());

        where:
        streamData                      | bufferLimit
        [0x01, 0x03, 0x00, 0x06, 0xaa]  | 3
        [0x01, 0x03, 0x00, 0x06, 0xaa]  | 6
        [0x06]                          | 3
    }

    def dataSeriesFromBuffer(ViewBuffer buffer) {
        List<Integer> series = new ArrayList<>(buffer.remaining());
        while (buffer.hasRemaining()) {
            series.add(buffer.get() & 0xff);
        }
        return series;
    }

    def dataSeriesFromMultiLists(List<List<Integer>> dataSeries) {
        return dataSeries.stream()
                .flatMap({ list -> list.stream()})
                .collect(Collectors.toList());
    }
}
