package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.StreamTimeoutException
import com.rposcro.jwavez.serial.exceptions.StreamMalformedException
import com.rposcro.jwavez.serial.exceptions.SerialPortException
import com.rposcro.jwavez.serial.rxtx.port.SerialPort
import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

import java.util.stream.Collectors

import static com.rposcro.jwavez.serial.TestUtils.dataFromBuffer
import static SerialFrameConstants.CATEGORY_ACK
import static SerialFrameConstants.CATEGORY_CAN
import static SerialFrameConstants.CATEGORY_NAK
import static SerialFrameConstants.CATEGORY_SOF
import static java.lang.Byte.toUnsignedInt

class ResponseStageDoerSpec extends Specification {

    static final ACK = toUnsignedInt(CATEGORY_ACK);
    static final NAK = toUnsignedInt(CATEGORY_NAK);
    static final CAN = toUnsignedInt(CATEGORY_CAN);
    static final SOF = toUnsignedInt(CATEGORY_SOF);

    @Shared
    def functionCode;
    @Shared
    def responseData;

    def receivedResponseData;
    def responseConsumer = { frameView -> receivedResponseData.addAll(dataFromBuffer(frameView)) };
    def serialPort;
    def rxTxConfiguration;

    def setupSpec() {
        functionCode = 0x4a;
        responseData = [0x01, 0x06, 0x01, functionCode, 0x02, 0x03, 0x04, 0x66];
    }

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        serialPort = new MockedSerialPort();
        receivedResponseData = [];
    }

    @Unroll
    def "handles expected inbound frame of #inboundData"() {
        given:
        def doer = makeDoer(inboundData);

        when:
        def result = doer.acquireResponse((byte) functionCode);

        then:
        result == ResponseStageResult.RESULT_OK;
        serialPort.outboundData == [ACK];
        receivedResponseData == responseData;

        where:
        inboundData                                                                                 | _
        [responseData]                                                                              | _
        singletonsOf(responseData)                                                                  | _
        [responseData + [0x06, 0x15]]                                                               | _
        [responseData, [0x06, 0x15]]                                                                | _
        [responseData.subList(0, 3), responseData.subList(3, responseData.size()), [0x06, 0x15]]    | _
    }

    @Unroll
    def "handles unexpected inbound frame of #inboundData"() {
        given:
        def doer = makeDoer(inboundData);

        when:
        def result = doer.acquireResponse((byte) functionCode);

        then:
        result == expResult;
        serialPort.outboundData == expOutData;
        doer.inboundStream.frameBuffer.position() == 0;
        doer.inboundStream.frameBuffer.limit() == 0;

        where:
        inboundData                             | expOutData   | expResult
        [[ACK]]                                 | [CAN]        | ResponseStageResult.RESULT_ODD_CATEGORY
        [[ACK] + responseData]                  | [CAN]        | ResponseStageResult.RESULT_ODD_CATEGORY
        [[ACK], responseData]                   | [CAN]        | ResponseStageResult.RESULT_ODD_CATEGORY
        [[NAK]]                                 | [CAN]        | ResponseStageResult.RESULT_ODD_CATEGORY
        [[CAN]]                                 | [CAN]        | ResponseStageResult.RESULT_ODD_CATEGORY
        [[SOF, 0x03, 0x01, 0x2a, 0xff]]         | [CAN]        | ResponseStageResult.RESULT_DIVERGENT_RESPONSE
        [[SOF, 0x03, 0x01, 0x2a, 0xff], [ACK]]  | [CAN]        | ResponseStageResult.RESULT_DIVERGENT_RESPONSE
        [[SOF], [0x03, 0x01, 0x2a, 0xff]]       | [CAN]        | ResponseStageResult.RESULT_DIVERGENT_RESPONSE
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

    @Unroll
    def "handles inbound frame exceptions #inboundData"() {
        given:
        def doer = makeDoer(inboundData);

        when:
        doer.acquireResponse((byte) functionCode);

        then:
        thrown expException;

        where:
        inboundData                     | expException
        [[0x01, 0x03, 0x00]]            | StreamTimeoutException
        [[0x00]]                        | StreamMalformedException
    }

    def "carries exceptions from port"() {
        given:
        def serialPort = Mock(SerialPort);
        def doer = ResponseStageDoer.builder()
                .inboundStream(FrameInboundStream.builder().serialPort(serialPort).build())
                .configuration(RxTxConfiguration.builder().build())
                .build();
        serialPort.readData(_) >> { buffer -> throw new SerialPortException("") };

        when:
        doer.acquireResponse((byte) 0x00);

        then:
        thrown SerialPortException;
    }

    def singletonsOf(List<Integer> data) {
        return data.stream()
            .map({value -> Collections.singletonList(value)})
            .collect(Collectors.toList());
    }

    def makeDoer(List<List<Integer>> frameData) {
        frameData.forEach({series -> serialPort.addSeries(series)});
        serialPort.reset();
        def inboundStream = FrameInboundStream.builder().serialPort(serialPort).configuration(rxTxConfiguration).build();
        def outboundStream = FrameOutboundStream.builder().serialPort(serialPort).build();
        return ResponseStageDoer.builder()
                .inboundStream(inboundStream)
                .outboundStream(outboundStream)
                .configuration(rxTxConfiguration)
                .responseHandler(responseConsumer)
                .build();
    }
}
