package com.rposcro.jwavez.serial.rxtx

import com.rposcro.jwavez.serial.exceptions.FatalSerialException
import com.rposcro.jwavez.serial.exceptions.SerialPortException
import com.rposcro.jwavez.serial.rxtx.port.SerialPort
import com.rposcro.jwavez.serial.rxtz.MockedSerialPort
import spock.lang.Specification

class RxTxRouterProcessSpec extends Specification {

    def serialPort;
    def rxTxConfiguration;

    def setup() {
        rxTxConfiguration = RxTxConfiguration.builder().build();
        serialPort = new MockedSerialPort();
    }

    def "fatal exception thrown when reconnection tries fail"() {
        given:
        def serialPort = Mock(SerialPort);
        def process = constructProcess([]);
        serialPort.readData(_) >> { buffer -> throw new SerialPortException("") };
        process.rxTxRouter.serialPort = serialPort;
        process.rxTxRouter.inboundStream.serialPort = serialPort;
        rxTxConfiguration.portReconnectDelayBias = 1;
        rxTxConfiguration.portReconnectDelayFactor = 0;

        when:
        process.run();

        then:
        thrown FatalSerialException;
    }

    def constructProcess(List<List<Integer>> inbounds) {
        inbounds.forEach({series -> serialPort.addSeries(series)});
        serialPort.reset();

        return RxTxRouterProcess.builder()
                .serialPort(serialPort)
                .configuration(rxTxConfiguration)
                .build();
    }
}
