package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.StreamTimeoutException
import com.rposcro.jwavez.serial.exceptions.StreamMalformedException
import com.rposcro.jwavez.serial.exceptions.SerialPortException
import com.rposcro.jwavez.serial.rxtx.port.SerialPort
import com.rposcro.jwavez.serial.rxtx.MockedSerialPort
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.TestUtils.byteBufferFromData
import static SerialFrameConstants.CATEGORY_ACK
import static SerialFrameConstants.CATEGORY_CAN
import static SerialFrameConstants.CATEGORY_NAK
import static SerialFrameConstants.CATEGORY_SOF
import static java.lang.Byte.toUnsignedInt

class RequestStageDoerSpec extends Specification {

    static final ACK = toUnsignedInt(CATEGORY_ACK);
    static final NAK = toUnsignedInt(CATEGORY_NAK);
    static final CAN = toUnsignedInt(CATEGORY_CAN);
    static final SOF = toUnsignedInt(CATEGORY_SOF);

    @Shared
    def requestData;

    def rxTxConfiguration;
    def serialPort;

    def setupSpec() {
        requestData = [SOF, 0x03, 0x00, 0x4a, 0xee];
    }

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        serialPort = new MockedSerialPort();
    }

    @Unroll
    def "sends request and receives answer as #inboundData"() {
        given:
        def reqBuffer = byteBufferFromData(requestData);
        def doer = makeDoer(inboundData);
        def expOutboundData = requestData + expLastOut;

        when:
        def result = doer.sendRequest(reqBuffer);

        then:
        result == expResult;
        serialPort.outboundData == expOutboundData;
        doer.inboundStream.frameBuffer.position() == expPos;
        doer.inboundStream.frameBuffer.limit() == expLim;

        where:
        inboundData                            | expPos | expLim | expLastOut | expResult
        [[0x06]]                               | 1      | 1      | []         | RequestStageResult.RESULT_OK
        [[], [], [], [0x06]]                   | 1      | 1      | []         | RequestStageResult.RESULT_OK
        [[0x06, 0x06, 0x15]]                   | 1      | 3      | []         | RequestStageResult.RESULT_OK
        [[0x15]]                               | 1      | 1      | []         | RequestStageResult.RESULT_NAK
        [[0x18]]                               | 1      | 1      | []         | RequestStageResult.RESULT_CAN
        [[0x01, 0x03, 0x01, 0x44, 0xff]]       | 0      | 0      | CAN        | RequestStageResult.RESULT_SOF
        [[0x01, 0x03, 0x01, 0x44, 0xff, 0x15]] | 0      | 0      | CAN        | RequestStageResult.RESULT_SOF
    }

    def "handles ack timeout"() {
        given:
        def reqBuffer = byteBufferFromData(requestData);
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
        def reqBuffer = byteBufferFromData(requestData);
        def doer = makeDoer(resData);

        when:
        doer.sendRequest(reqBuffer);

        then:
        thrown expException;

        where:
        resData              | expException
        [[0x01, 0x03, 0x00]] | StreamTimeoutException
        [[0x00]]             | StreamMalformedException
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

    def makeDoer(List<List<Integer>> frameData) {
        frameData.forEach({ series -> serialPort.addSeries(series) });
        serialPort.reset();
        def inboundStream = FrameInboundStream.builder().serialPort(serialPort).configuration(rxTxConfiguration).build();
        def outboundStream = FrameOutboundStream.builder().serialPort(serialPort).build();
        return RequestStageDoer.builder()
                .inboundStream(inboundStream)
                .outboundStream(outboundStream)
                .configuration(rxTxConfiguration)
                .build();
    }
}
