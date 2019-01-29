package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException
import com.rposcro.jwavez.serial.exceptions.OddFrameException
import com.rposcro.jwavez.serial.exceptions.SerialPortException
import com.rposcro.jwavez.serial.rxtx.port.SerialPort
import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN

class RequestStageDoerSpec extends Specification {

    def rxTxConfiguration;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
    }

    @Unroll
    def "handles incoming frame of #resData"() {
        given:
        def reqData = [0x01, 0x03, 0x00, 0x4a, 0xee];
        def reqBuffer = makeBuffer(reqData);
        def doer = makeDoer(resData);
        def expOutData = reqData + expLastOut;

        when:
        def result = doer.sendRequest(reqBuffer);

        then:
        result == expResult;
        doer.inboundStream.frameBuffer.position() == expPos;
        doer.inboundStream.frameBuffer.limit() == expLim;
        doer.outboundStream.serialPort.outboundData == expOutData;

        where:
        resData                                | expPos | expLim | expLastOut   | expResult
        [[0x06]]                               | 1      | 1      | []           | RequestStageResult.RESULT_OK
        [[], [], [], [0x06]]                   | 1      | 1      | []           | RequestStageResult.RESULT_OK
        [[0x06, 0x06, 0x15]]                   | 1      | 3      | []           | RequestStageResult.RESULT_OK
        [[0x15]]                               | 1      | 1      | []           | RequestStageResult.RESULT_NAK
        [[0x18]]                               | 1      | 1      | []           | RequestStageResult.RESULT_CAN
        [[0x01, 0x03, 0x01, 0x44, 0xff]]       | 0      | 0      | CATEGORY_CAN | RequestStageResult.RESULT_SOF
        [[0x01, 0x03, 0x01, 0x44, 0xff, 0x15]] | 0      | 0      | CATEGORY_CAN | RequestStageResult.RESULT_SOF
    }

    @Unroll
    def "handles ack timeout"() {
        given:
        def reqData = [0x01, 0x03, 0x00, 0x4a, 0xee];
        def reqBuffer = makeBuffer(reqData);
        def resData = [[]];
        def doer = makeDoer(resData);
        rxTxConfiguration.frameAckTimeout = 10;

        when:
        def result = doer.sendRequest(reqBuffer);

        then:
        result == RequestStageResult.RESULT_ACK_TIMEOUT;
    }

    @Unroll
    def "handles inbound frame exceptions #resData"() {
        given:
        def reqData = [0x01, 0x03, 0x00, 0x4a, 0xee];
        def reqBuffer = makeBuffer(reqData);
        def doer = makeDoer(resData);

        when:
        doer.sendRequest(reqBuffer);

        then:
        thrown expException;

        where:
        resData                         | expException
        [[0x01, 0x03, 0x00]]            | FrameTimeoutException
        [[0x00]]                        | OddFrameException
    }

    def "carries exceptions from port"() {
        given:
        def serialPort = Mock(SerialPort);
        def doer = RequestStageDoer.builder()
                .outboundStream(FrameOutboundStream.builder().serialPort(serialPort).build())
                .build();
        serialPort.writeData(_) >> { buffer -> throw new SerialPortException("") };

        when:
        doer.sendRequest(ByteBuffer.allocate(1));

        then:
        thrown SerialPortException;
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
        return RequestStageDoer.builder().inboundStream(inboundStream).outboundStream(outboundStream).configuration(rxTxConfiguration).build();
    }
}
