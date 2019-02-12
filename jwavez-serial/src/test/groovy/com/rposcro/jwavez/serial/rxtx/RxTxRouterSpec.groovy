package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.FatalSerialException
import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException
import com.rposcro.jwavez.serial.exceptions.OddFrameException
import com.rposcro.jwavez.serial.exceptions.RequestFlowException
import com.rposcro.jwavez.serial.exceptions.SerialPortException
import com.rposcro.jwavez.serial.rxtx.port.SerialPort
import spock.lang.Unroll
import spock.lang.Shared

import java.util.concurrent.ExecutionException
import java.util.stream.IntStream

import static com.rposcro.jwavez.serial.TestUtils.frameBufferFromData
import static com.rposcro.jwavez.serial.TestUtils.dataFromBuffer
import static SerialFrameConstants.CATEGORY_ACK
import static SerialFrameConstants.CATEGORY_NAK
import static SerialFrameConstants.CATEGORY_CAN
import static SerialFrameConstants.CATEGORY_SOF

import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import spock.lang.Specification

import static java.lang.Byte.toUnsignedInt

class RxTxRouterSpec extends Specification {

    static final ACK = toUnsignedInt(CATEGORY_ACK);
    static final NAK = toUnsignedInt(CATEGORY_NAK);
    static final CAN = toUnsignedInt(CATEGORY_CAN);
    static final SOF = toUnsignedInt(CATEGORY_SOF);

    @Shared
    def functionCode;
    @Shared
    def responseData;
    @Shared
    def requestData;
    @Shared
    def callbackData;
    @Shared
    def partialData;
    @Shared
    def whateverData;

    def receivedResponseData;
    def receivedCallbackData;
    def responseConsumer = { frameView -> receivedResponseData.addAll(dataFromBuffer(frameView)) };
    def callbackConsumer = { frameView -> receivedCallbackData.addAll(dataFromBuffer(frameView)) };
    def rxTxConfiguration;
    def serialPort;

    def setupSpec() {
        functionCode = 0x44;
        responseData = [SOF, 0x06, 0x01, functionCode, 0x02, 0x03, 0x04, 0x66];
        requestData = [SOF, 0x04, 0x00, functionCode, 0x02, 0x55];
        callbackData = [SOF, 0x04, 0x00, 0x5a, 0x02, 0x77];
        partialData = [SOF, 0x04, 0x00, 0x5a];
        whateverData = [0x99, 0x88, 0x77, 0x66];
    }

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        serialPort = new MockedSerialPort();
        receivedResponseData = [];
        receivedCallbackData = [];
    }

    def "no traffic"() {
        given:
        def controller = constructController([]);

        when:
        controller.runOnce();

        then:
        serialPort.outboundData.isEmpty();
        receivedResponseData.isEmpty();
        receivedCallbackData.isEmpty();
    }

    @Unroll
    def "sends request without response where inbound is #inboundData"() {
        given:
        def expOutboundData = requestData;
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
                .frameData(frameBufferFromData(requestData))
                .responseExpected(false)
                .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();

        then:
        serialPort.outboundData == expOutboundData;
        receivedResponseData.isEmpty();
        receivedCallbackData.isEmpty();

        where:
        inboundData                                 | _
        [[], [ACK]]                                 | _
        [[], [ACK], whateverData]                   | _
        [[], [ACK] + whateverData]                  | _
        [[], [ACK, NAK]]                            | _
    }

    @Unroll
    def "sends request and receives response when inbound is #inboundData"() {
        given:
        def outboundData = requestData + [ACK];
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
            .frameData(frameBufferFromData(requestData))
            .responseExpected(true)
            .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();

        then:
        serialPort.outboundData == outboundData;
        receivedResponseData == responseData;
        receivedCallbackData.isEmpty();

        where:
        inboundData                                                     | _
        [[], [ACK], responseData]                                       | _
        [[], [ACK] + responseData]                                      | _
        [[], [ACK], responseData, whateverData]                         | _
        [[], [ACK], responseData, [CAN]]                                | _
    }

    @Unroll
    def "receives callback, next sends request and receives response, inbound is #inboundData"() {
        given:
        def outboundData = [ACK] + requestData + [ACK];
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
                .frameData(frameBufferFromData(requestData))
                .responseExpected(true)
                .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();

        then:
        serialPort.outboundData == outboundData;
        receivedResponseData == responseData;
        receivedCallbackData == callbackData;

        where:
        inboundData                                                             | _
        [callbackData, [], [ACK], responseData]                                 | _
        [callbackData, [], [ACK] + responseData]                                | _
        [callbackData, [], [ACK], responseData, whateverData]                   | _
        [callbackData, [], [ACK], responseData, [CAN]]                          | _
    }

    @Unroll
    def "receives multiple callbacks in a row #inboundData"() {
        given:
        def controller = constructController(inboundData);

        when:
        controller.runOnce();

        then:
        serialPort.outboundData == expOutboundData;
        receivedResponseData.isEmpty();
        receivedCallbackData == expCallbackData;

        where:
        inboundData                   | expOutboundData | expCallbackData
        [callbackData]                | [ACK]           | callbackData
        [callbackData, callbackData]  | [ACK, ACK]      | callbackData + callbackData
        [callbackData + callbackData] | [ACK, ACK]      | callbackData + callbackData
    }

    @Unroll
    def "while idle receives unsolicited odd frame of #inboundData"() {
        given:
        def controller = constructController(inboundData);

        when:
        controller.runOnce();

        then:
        serialPort.outboundData == [CAN];
        receivedResponseData.isEmpty();
        receivedCallbackData.isEmpty();
        controller.inboundStream.frameBuffer.position() == 0;
        controller.inboundStream.frameBuffer.limit() == 0;

        where:
        inboundData                     | _
        [[ACK]]                         | _
        [[ACK, ACK]]                    | _
        [[CAN]]                         | _
        [[CAN, NAK, ACK]]               | _
        [[NAK, NAK, ACK], responseData] | _
        [responseData, [ACK]]           | _
        [responseData, responseData]    | _
    }

    @Unroll
    def "retry request when NAK or CAN #inboundData"() {
        given:
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
                .responseExpected(expResponse)
                .frameData(frameBufferFromData(requestData))
                .build();
        rxTxConfiguration.requestRetryDelayBias = 0;
        rxTxConfiguration.requestRetryDelayFactor = 0;
        def expOutboundData = repeatData(requestData, attempts) + (expResponse ? [ACK] : []);

        when:
        controller.scheduleRequest(frameRequest);
        for (int i = 0; i < attempts; i++) {
            controller.runOnce();
        }

        then:
        serialPort.outboundData == expOutboundData;
        receivedResponseData == (expResponse ? responseData : []);
        receivedCallbackData.isEmpty();

        where:
        inboundData                                         | attempts  | expResponse
        [[], [NAK], [], [ACK]]                              | 2         | false
        [[], [NAK], [], [ACK], whateverData]                | 2         | false
        [[], [NAK], [], [NAK], [], [ACK]]                   | 3         | false
        [[], [CAN], [], [ACK]]                              | 2         | false
        [[], [CAN], [], [CAN], [], [ACK]]                   | 3         | false
        [[], [NAK], [], [CAN], [], [ACK]]                   | 3         | false
        [[], [CAN], [], [NAK], [], [ACK]]                   | 3         | false
        [[], [NAK], [], [ACK]]                              | 2         | false
        [[], [NAK], [], [ACK], responseData]                | 2         | true
        [[], [NAK], [], [ACK], responseData, whateverData]  | 2         | true
        [[], [NAK], [], [NAK], [], [ACK], responseData]     | 3         | true
        [[], [CAN], [], [ACK], responseData]                | 2         | true
        [[], [CAN], [], [CAN], [], [ACK], responseData]     | 3         | true
        [[], [NAK], [], [CAN], [], [ACK], responseData]     | 3         | true
        [[], [CAN], [], [NAK], [], [ACK], responseData]     | 3         | true
        [[], [NAK], [], [ACK], responseData]                | 2         | true
    }

    @Unroll
    def "retry request when race condition is detected #inboundData"() {
        given:
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
                .responseExpected(expResponse)
                .frameData(frameBufferFromData(requestData))
                .build();
        rxTxConfiguration.requestRetryDelayBias = 0;
        rxTxConfiguration.requestRetryDelayFactor = 0;
        def expOutboundData = requestData + [CAN] + requestData + (expResponse ? [ACK] : []);

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();
        controller.runOnce();

        then:
        serialPort.outboundData == expOutboundData;
        receivedResponseData == (expResponse ? responseData : []);
        receivedCallbackData.isEmpty();

        where:
        inboundData                                                     | expResponse
        [[], responseData, [], [], [ACK]]                               | false
        [[], responseData, [], [], [ACK], whateverData]                 | false
        [[], responseData, [], [], [ACK], callbackData]                 | false
        [[], responseData, [], [], [ACK], callbackData, whateverData]   | false
        [[], responseData, [], [], [ACK], responseData]                 | true
        [[], responseData, [], [], [ACK], responseData, whateverData]   | true
    }

    @Unroll
    def "request flow exception thrown when retry limit is reached"() {
        given:
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
                .responseExpected(false)
                .frameData(frameBufferFromData(requestData))
                .build();
        rxTxConfiguration.requestRetryDelayBias = 0;
        rxTxConfiguration.requestRetryDelayFactor = 0;
        rxTxConfiguration.requestRetriesMaxCount = 1;

        when:
        controller.scheduleRequest(frameRequest);
        controller.runOnce();
        controller.runOnce();
        controller.futureResponse.get();

        then:
        controller.futureResponse.isCompletedExceptionally();
        def e = thrown(ExecutionException);
        e.cause.getClass() == RequestFlowException;

        where:
        inboundData                              | _
        [[], [NAK], [], [NAK], []]               | _
        [[], [NAK], [], [NAK], [], whateverData] | _
    }

    @Unroll
    def "odd frame exception thrown when inbound is malformed in receive stage"() {
        given:
        def controller = constructController(inboundData);

        when:
        controller.receiveStage();
        controller.receiveStage();

        then:
        receivedResponseData.isEmpty();
        receivedCallbackData == expCallbackData;
        thrown OddFrameException;

        where:
        inboundData                             | expCallbackData
        [whateverData]                          | []
        [[], whateverData]                      | []
        [callbackData, whateverData]            | callbackData
    }

    @Unroll
    def "odd frame exception thrown when inbound is malformed in transmit stage"() {
        given:
        def controller = constructController(inboundData);
        def frameRequest = FrameRequest.builder()
                .responseExpected(true)
                .frameData(frameBufferFromData(requestData))
                .build();

        when:
        controller.scheduleRequest(frameRequest);
        controller.transmitStage();
        controller.transmitStage();

        then:
        receivedResponseData.isEmpty();
        receivedCallbackData.isEmpty();
        thrown OddFrameException;

        where:
        inboundData                             | _
        [whateverData]                          | _
        [[], whateverData]                      | _
        [[ACK], whateverData]                   | _
        [[ACK], [], whateverData]               | _
    }

    @Unroll
    def "time out exception thrown when in receive stage"() {
        given:
        def controller = constructController(inboundData);
        rxTxConfiguration.requestRetryDelayBias = 0;
        rxTxConfiguration.requestRetryDelayFactor = 0;
        rxTxConfiguration.frameCompleteTimeout = 1;

        when:
        controller.receiveStage();
        controller.receiveStage();

        then:
        receivedResponseData.isEmpty();
        receivedCallbackData == expCallbackData;
        thrown FrameTimeoutException;

        where:
        inboundData                             | expCallbackData
        [partialData]                           | []
        [[], partialData]                       | []
        [callbackData, partialData]             | callbackData
    }

    @Unroll
    def "time out exception thrown when in transmit stage"() {
        given:
        def controller = constructController(inboundData);
        rxTxConfiguration.frameCompleteTimeout = 1;

        when:
        controller.scheduleRequest(FrameRequest.builder()
                .responseExpected(true).frameData(frameBufferFromData(requestData)).build());
        controller.transmitStage();
        controller.scheduleRequest(FrameRequest.builder()
                .responseExpected(true).frameData(frameBufferFromData(requestData)).build());
        controller.transmitStage();

        then:
        receivedResponseData == expResponseData;
        receivedCallbackData.isEmpty();
        thrown FrameTimeoutException;

        where:
        inboundData                                 | expResponseData
        [partialData]                               | []
        [[], partialData]                           | []
        [[ACK], partialData]                        | []
        [[ACK], [], partialData]                    | []
        [[], [ACK], partialData]                    | []
        [[ACK], responseData, [ACK], partialData]   | responseData
    }

    def "fatal exception thrown when reconnection tries fail"() {
        given:
        def serialPort = Mock(SerialPort);
        def controller = constructController([]);
        serialPort.readData(_) >> { buffer -> throw new SerialPortException("") };
        controller.serialPort = serialPort;
        controller.inboundStream.serialPort = serialPort;
        rxTxConfiguration.portReconnectDelayBias = 1;
        rxTxConfiguration.portReconnectDelayFactor = 0;

        when:
        controller.run();

        then:
        thrown FatalSerialException;
    }


    def repeatData(List<Integer> data, int repeats) {
        def result = [];
        IntStream.range(0, repeats).forEach({ i -> result.addAll(data) });
        return result;
    }

    def constructController(List<List<Integer>> inbounds) {
        inbounds.forEach({series -> serialPort.addSeries(series)});
        serialPort.reset();

        return RxTxRouter.builder()
            .serialPort(serialPort)
            .configuration(rxTxConfiguration)
            .responseHandler(responseConsumer)
            .callbackHandler(callbackConsumer)
            .build();
    }
}
