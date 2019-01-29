package com.rposcro.jwavez.serial.rxtx

import spock.lang.Unroll
import spock.lang.Shared

import static com.rposcro.jwavez.serial.TestUtils.bufferFromData
import static com.rposcro.jwavez.serial.TestUtils.dataFromBuffer
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN

import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import spock.lang.Specification

import static java.lang.Byte.toUnsignedInt

class RxTxControllerSpec extends Specification {

    static final ACK = toUnsignedInt(CATEGORY_ACK);
    static final NAK = toUnsignedInt(CATEGORY_NAK);
    static final CAN = toUnsignedInt(CATEGORY_CAN);

    @Shared
    def functionCode;
    @Shared
    def responseData;
    @Shared
    def requestData;
    @Shared
    def callbackData;

    def receivedResponseData;
    def receivedCallbackData;
    def responseConsumer = { frameView -> receivedResponseData = dataFromBuffer(frameView) };
    def callbackConsumer = { frameView -> receivedCallbackData = dataFromBuffer(frameView) };
    def serialPort;

    def setupSpec() {
        functionCode = 0x44;
        responseData = [0x01, 0x06, 0x01, functionCode, 0x02, 0x03, 0x04, 0x66];
        requestData = [0x01, 0x04, 0x00, functionCode, 0x02, 0x55];
        callbackData = [0x01, 0x04, 0x00, 0x5a, 0x02, 0x77];
    }

    def setup() {
        serialPort = new MockedSerialPort();
    }

    @Unroll
    def "sends request without response"() {
        given:
        def outboundStream = requestData;
        def controller = constructController(inboundStream);
        def frameRequest = FrameRequest.builder()
                .frameData(bufferFromData(requestData))
                .responseExpected(false)
                .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();

        then:
        serialPort.outboundData == outboundStream;
        receivedResponseData == null;
        receivedCallbackData == null;

        where:
        inboundStream                                   | _
        [[], [ACK]]                                     | _
        [[], [ACK], [0x01, 0x03, 0x00, 0x55, 0xbb]]     | _
        [[], [ACK, 0x01, 0x03, 0x00, 0x55, 0xbb]]       | _
        [[], [ACK, NAK]]                                | _
    }

    @Unroll
    def "sends request and receives response"() {
        given:
        def outboundStream = requestData + [ACK];
        def controller = constructController(inboundStream);
        def frameRequest = FrameRequest.builder()
            .frameData(bufferFromData(requestData))
            .responseExpected(true)
            .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();

        then:
        serialPort.outboundData == outboundStream;
        receivedResponseData == responseData;
        receivedCallbackData == null;

        where:
        inboundStream                                                   | _
        [[], [ACK], responseData]                                       | _
        [[], [ACK] + responseData]                                      | _
        [[], [ACK], responseData, [0x01, 0x03, 0x00, 0x55, 0xbb]]       | _
        [[], [ACK], responseData, [CAN]]                                | _
    }

    @Unroll
    def "receives callback, next sends request and receives response"() {
        given:
        def outboundStream = [ACK] + requestData + [ACK];
        def controller = constructController(inboundStream);
        def frameRequest = FrameRequest.builder()
                .frameData(bufferFromData(requestData))
                .responseExpected(true)
                .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();

        then:
        serialPort.outboundData == outboundStream;
        receivedResponseData == responseData;
        receivedCallbackData == callbackData;

        where:
        inboundStream                                                                 | _
        [callbackData, [], [ACK], responseData]                                       | _
        [callbackData, [], [ACK] + responseData]                                      | _
        [callbackData, [], [ACK], responseData, [0x01, 0x03, 0x00, 0x55, 0xbb]]       | _
        [callbackData, [], [ACK], responseData, [CAN]]                                | _
    }


    def constructController(List<List<Integer>> inbounds) {
        inbounds.forEach({series -> serialPort.addSeries(series)});
        serialPort.reset();

        return RxTxController.builder()
            .serialPort(serialPort)
            .configuration(RxTxConfiguration.builder().build())
            .responseHandler(responseConsumer)
            .callbackHandler(callbackConsumer)
            .build();
    }
}
