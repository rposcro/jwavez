package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.StreamTimeoutException
import com.rposcro.jwavez.serial.exceptions.StreamMalformedException
import com.rposcro.jwavez.serial.exceptions.SerialPortException
import com.rposcro.jwavez.serial.rxtx.port.SerialPort
import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

import static com.rposcro.jwavez.serial.TestUtils.dataFromBuffer
import static SerialFrameConstants.CATEGORY_ACK
import static SerialFrameConstants.CATEGORY_CAN
import static SerialFrameConstants.CATEGORY_NAK
import static SerialFrameConstants.CATEGORY_SOF
import static java.lang.Byte.toUnsignedInt

class IdleStageDoerSpec extends Specification {

    static final ACK = toUnsignedInt(CATEGORY_ACK);
    static final NAK = toUnsignedInt(CATEGORY_NAK);
    static final CAN = toUnsignedInt(CATEGORY_CAN);
    static final SOF = toUnsignedInt(CATEGORY_SOF);

    @Shared
    def callbackData;

    def rxTxConfiguration;
    def receivedCallbackData;
    def callbackConsumer = { frameView -> receivedCallbackData.addAll(dataFromBuffer(frameView)) };
    def serialPort;

    def setupSpec() {
        callbackData = [SOF, 0x03, 0x00, 0x44, 0xee];
    }

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        serialPort = new MockedSerialPort();
        receivedCallbackData = [];
    }

    def "handles silence"() {
        given:
        def doer = makeDoer([]);

        when:
        IdleStageResult result = doer.checkInbound();

        then:
        result == IdleStageResult.RESULT_SILENCE;
        doer.inboundStream.frameBuffer.position() == 0;
        doer.inboundStream.frameBuffer.limit() == 0;
    }

    @Unroll
    def "handles correct inbound frame of #inboundData"() {
        given:
        def doer = makeDoer(inboundData);

        when:
        IdleStageResult result = doer.checkInbound();

        then:
        result == expResult;
        doer.inboundStream.frameBuffer.position() == expPosition;
        doer.inboundStream.frameBuffer.limit() == expLimit;
        serialPort.outboundData == expOut;
        receivedCallbackData == callbackData;

        where:
        inboundData          | expPosition | expLimit | expOut | expResult
        callbackData         | 5           | 5        | [ACK]  | IdleStageResult.RESULT_CALLBACK_HANDLED
        callbackData + [CAN] | 5           | 6        | [ACK]  | IdleStageResult.RESULT_CALLBACK_HANDLED
    }

    @Unroll
    def "handles incorrect inbound frame of #inboundData"() {
        given:
        def doer = makeDoer(inboundData);

        when:
        IdleStageResult result = doer.checkInbound();

        then:
        result == expResult;
        doer.inboundStream.frameBuffer.position() == expPosition;
        doer.inboundStream.frameBuffer.limit() == expLimit;
        serialPort.outboundData == expOut;
        receivedCallbackData == [];

        where:
        inboundData                         | expPosition | expLimit | expOut | expResult
        [ACK]                               | 0           | 0        | [CAN]  | IdleStageResult.RESULT_ODD_INCOME
        [CAN]                               | 0           | 0        | [CAN]  | IdleStageResult.RESULT_ODD_INCOME
        [NAK]                               | 0           | 0        | [CAN]  | IdleStageResult.RESULT_ODD_INCOME
        [SOF, 0x03, 0x01, 0x44, 0xee]       | 0           | 0        | [CAN]  | IdleStageResult.RESULT_ODD_INCOME
        [SOF, 0x03, 0x01, 0x44, 0xee, 0x18] | 0           | 0        | [CAN]  | IdleStageResult.RESULT_ODD_INCOME
    }

    @Unroll
    def "handles inbound frame exceptions #inboundData"() {
        given:
        def doer = makeDoer(inboundData);
        rxTxConfiguration.frameCompleteTimeout = 10;

        when:
        doer.checkInbound();

        then:
        thrown expException;

        where:
        inboundData                    | expException
        [0x01, 0x03, 0x00]             | StreamTimeoutException
        [0x55, 0x03, 0x00, 0x44, 0xee] | StreamMalformedException
    }

    def "carries exceptions from port"() {
        given:
        def serialPort = Mock(SerialPort);
        def doer = IdleStageDoer.builder()
                .inboundStream(FrameInboundStream.builder().serialPort(serialPort).build())
                .build();
        serialPort.readData(_) >> { buffer -> throw new SerialPortException("") };

        when:
        doer.checkInbound();

        then:
        thrown SerialPortException;
    }

    def makeDoer(List<Integer> frameData) {
        serialPort.addSeries(frameData).reset();
        def inboundStream = FrameInboundStream.builder().serialPort(serialPort).configuration(rxTxConfiguration).build();
        def outboundStream = FrameOutboundStream.builder().serialPort(serialPort).build();
        return IdleStageDoer.builder()
                .inboundStream(inboundStream)
                .outboundStream(outboundStream)
                .callbackHandler(callbackConsumer)
                .build();
    }
}
