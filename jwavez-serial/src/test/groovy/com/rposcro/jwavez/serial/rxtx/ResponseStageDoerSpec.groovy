package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException
import com.rposcro.jwavez.serial.exceptions.OddFrameException
import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import com.rposcro.jwavez.serial.utils.ViewBuffer
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN

class ResponseStageDoerSpec extends Specification {

    def rxTxConfiguration;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
    }

    @Unroll
    def "handles incoming expected frame of #resData"() {
        given:
        def doer = makeDoer(resData);
        ViewBuffer responseBuffer;
        doer.responseHandler = { buffer -> responseBuffer = buffer};

        when:
        def result = doer.acquireResponse((byte) 0x4a);

        then:
        result == ResponseStageResult.RESULT_OK;
        doer.outboundStream.serialPort.outboundData == [CATEGORY_ACK];
        dataSeriesFromBuffer(responseBuffer) == [0x01, 0x03, 0x01, 0x4a, 0xff];

        where:
        resData                                             | _
        [[0x01, 0x03, 0x01, 0x4a, 0xff]]                    | _
        [[0x01], [0x03], [0x01], [0x4a], [0xff]]            | _
        [[0x01, 0x03, 0x01, 0x4a, 0xff, 0x06, 0x15]]        | _
        [[0x01, 0x03, 0x01, 0x4a, 0xff], [0x06, 0x15]]      | _
        [[0x01, 0x03, 0x01], [0x4a, 0xff], [0x06, 0x15]]    | _
    }

    @Unroll
    def "handles incoming unexpected frame of #resData"() {
        given:
        def doer = makeDoer(resData);

        when:
        def result = doer.acquireResponse((byte) expFnc);

        then:
        result == expResult;
        doer.inboundStream.frameBuffer.position() == 0;
        doer.inboundStream.frameBuffer.limit() == 0;
        doer.outboundStream.serialPort.outboundData == expOutData;

        where:
        resData                                  | expFnc | expOutData      | expResult
        [[0x06]]                                 | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_ODD_CATEGORY
        [[0x06, 0x01, 0x03, 0x01, 0x4a, 0xff]]   | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_ODD_CATEGORY
        [[0x06], [0x01, 0x03, 0x01, 0x4a, 0xff]] | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_ODD_CATEGORY
        [[0x15]]                                 | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_ODD_CATEGORY
        [[0x18]]                                 | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_ODD_CATEGORY
        [[0x01, 0x03, 0x01, 0x2a, 0xff]]         | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_DIVERGENT_RESPONSE
        [[0x01, 0x03, 0x01, 0x2a, 0xff], [0x06]] | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_DIVERGENT_RESPONSE
        [[0x01], [0x03, 0x01, 0x2a, 0xff]]       | 0x4a   | [CATEGORY_CAN]  | ResponseStageResult.RESULT_DIVERGENT_RESPONSE
    }

    @Unroll
    def "handles inbound frame exceptions #resData"() {
        given:
        def doer = makeDoer(resData);

        when:
        doer.acquireResponse((byte) 0x4a);

        then:
        thrown expException;

        where:
        resData                         | expException
        [[0x01, 0x03, 0x00]]            | FrameTimeoutException
        [[0x00]]                        | OddFrameException
    }

    def "handles response timeout"() {
        given:
        def resData = [[], [], []];
        def doer = makeDoer(resData);

        when:
        rxTxConfiguration.frameResponseTimeout = 10;
        def result = doer.acquireResponse((byte) 0x4a);

        then:
        result == ResponseStageResult.RESULT_RESPONSE_TIMEOUT;
    }

    def dataSeriesFromBuffer(ViewBuffer buffer) {
        List<Integer> series = new ArrayList<>(buffer.remaining());
        while (buffer.hasRemaining()) {
            series.add(buffer.get() & 0xff);
        }
        return series;
    }

    def makeBuffer(List<Integer> bufData) {
        def buffer = ByteBuffer.allocate(256);
        bufData.forEach({val -> buffer.put((byte) val)});
        buffer.limit(buffer.position());
        buffer.position(0);
    }

    def makeDoer(List<List<Integer>> frameData) {
        def port = new MockedSerialPort();
        frameData.forEach({series -> port.addSeries(series)});
        port.reset();
        def inboundStream = FrameInboundStream.builder().serialPort(port).configuration(rxTxConfiguration).build();
        def outboundStream = FrameOutboundStream.builder().serialPort(port).build();
        return ResponseStageDoer.builder().inboundStream(inboundStream).outboundStream(outboundStream).configuration(rxTxConfiguration).build();
    }
}
