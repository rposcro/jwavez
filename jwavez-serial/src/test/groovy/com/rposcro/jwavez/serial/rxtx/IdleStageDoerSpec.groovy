package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.FrameTimeoutException
import com.rposcro.jwavez.serial.exceptions.OddFrameException
import com.rposcro.jwavez.serial.rxtz.MockedSerialConnection
import com.rposcro.jwavez.serial.utils.ViewBuffer
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN

class IdleStageDoerSpec extends Specification {

    def rxTxConfiguration;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
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
    def "handles incoming frame of #frameData"() {
        given:
        def doer = makeDoer(frameData);
        ViewBuffer frameReceived;
        doer.callbackHandler = { frameView -> frameReceived = frameView };

        when:
        IdleStageResult result = doer.checkInbound();

        then:
        result == expResult;
        doer.inboundStream.frameBuffer.position() == expPosition;
        doer.inboundStream.frameBuffer.limit() == expLimit;
        doer.outboundStream.serialConnection.outboundData.size() == 1;
        doer.outboundStream.serialConnection.outboundData.get(0) == expOut;

        where:
        frameData                             | expPosition   | expLimit  | expOut        | expResult
        [0x01, 0x03, 0x00, 0x44, 0xee]        | 5             | 5         | CATEGORY_ACK  | IdleStageResult.RESULT_HANDLED
        [0x01, 0x03, 0x00, 0x44, 0xee, 0x15]  | 5             | 6         | CATEGORY_ACK  | IdleStageResult.RESULT_HANDLED
        [0x06]                                | 0             | 0         | CATEGORY_CAN  | IdleStageResult.RESULT_ODD_INCOME
        [0x15]                                | 0             | 0         | CATEGORY_CAN  | IdleStageResult.RESULT_ODD_INCOME
        [0x18]                                | 0             | 0         | CATEGORY_CAN  | IdleStageResult.RESULT_ODD_INCOME
        [0x01, 0x03, 0x01, 0x44, 0xee]        | 0             | 0         | CATEGORY_CAN  | IdleStageResult.RESULT_ODD_INCOME
        [0x01, 0x03, 0x01, 0x44, 0xee, 0x18]  | 0             | 0         | CATEGORY_CAN  | IdleStageResult.RESULT_ODD_INCOME
    }

    @Unroll
    def "handles inbound frame exceptions #frameData"() {
        given:
        def doer = makeDoer(frameData);
        rxTxConfiguration.frameCompleteTimeout = 10;

        when:
        doer.checkInbound();

        then:
        thrown expException;

        where:
        frameData                       | expException
        [0x01, 0x03, 0x00]              | FrameTimeoutException
        [0x55, 0x03, 0x00, 0x44, 0xee]  | OddFrameException
    }


    def makeDoer(List<Integer> frameData) {
        def connection = new MockedSerialConnection(frameData);
        connection.reset();
        def inboundStream = FrameInboundStream.builder().serialConnection(connection).configuration(rxTxConfiguration).build();
        def outboundStream = FrameOutboundStream.builder().serialConnection(connection).build();
        return IdleStageDoer.builder().inboundStream(inboundStream).outboundStream(outboundStream).build();
    }
}
