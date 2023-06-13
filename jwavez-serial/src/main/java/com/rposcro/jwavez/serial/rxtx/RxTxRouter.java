package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.core.utils.ObjectsUtil.orDefault;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.utils.BufferUtil.bufferToString;
import static java.lang.System.currentTimeMillis;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.StreamTimeoutException;
import com.rposcro.jwavez.serial.exceptions.StreamMalformedException;
import com.rposcro.jwavez.serial.exceptions.StreamFlowException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.exceptions.StreamException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;

import java.util.concurrent.Semaphore;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RxTxRouter {

    private SerialPort serialPort;
    private RxTxConfiguration configuration;
    private IdleStageDoer idleStageDoer;
    private RequestStageDoer requestStageDoer;
    private ResponseStageDoer responseStageDoer;
    private FrameInboundStream inboundStream;
    private FrameOutboundStream outboundStream;

    private final Semaphore outboundLock;

    private SerialRequest serialRequest;
    private int retransmissionCounter;
    private long retransmissionTime;
    private boolean transmissionSuccess;

    @Builder
    public RxTxRouter(
            RxTxConfiguration configuration,
            SerialPort serialPort,
            ResponseHandler responseHandler,
            CallbackHandler callbackHandler) {
        this();
        this.configuration = orDefault(configuration, RxTxConfiguration::defaultConfiguration);
        this.serialPort = serialPort;

        this.inboundStream = FrameInboundStream.builder()
                .configuration(this.configuration)
                .serialPort(serialPort)
                .build();
        this.outboundStream = FrameOutboundStream.builder()
                .serialPort(serialPort)
                .build();

        this.idleStageDoer = IdleStageDoer.builder()
                .inboundStream(inboundStream)
                .outboundStream(outboundStream)
                .callbackHandler(orDefault(callbackHandler, this::handleCallback))
                .build();
        this.requestStageDoer = RequestStageDoer.builder()
                .inboundStream(inboundStream)
                .outboundStream(outboundStream)
                .configuration(this.configuration)
                .build();
        this.responseStageDoer = ResponseStageDoer.builder()
                .inboundStream(inboundStream)
                .outboundStream(outboundStream)
                .configuration(this.configuration)
                .responseHandler(orDefault(responseHandler, this::handleResponse))
                .build();
    }

    private RxTxRouter() {
        this.outboundLock = new Semaphore(1);
        this.retransmissionTime = Long.MAX_VALUE;
    }

    public void runUnlessRequestSent(SerialRequest serialRequest) throws RxTxException {
        outboundLock.acquireUninterruptibly();
        try {
            enqueueRequest(serialRequest);
            while (transmissionAwaiting()) {
                runSingleCycle();
            }
            if (!transmissionSuccess) {
                throw new StreamFlowException("Failed to send request!");
            }
        } finally {
            serialRequest.getFrameData().dispose();
            deactivateTransmission();
        }
    }

    public void runSingleCycle() throws RxTxException {
        try {
            receiveStage();
            transmitStage();
            Thread.sleep(configuration.getRouterPollDelay());
        } catch (StreamMalformedException e) {
            outboundStream.writeNAK();
            inboundStream.purgeStream();
        } catch (StreamTimeoutException e) {
            outboundStream.writeCAN();
            inboundStream.purgeStream();
        } catch (StreamException e) {
            outboundStream.writeCAN();
            inboundStream.purgeStream();
        } catch (InterruptedException e) {
            log.info("Router's thread interrupted");
        }
    }

    public void purgeInput() throws SerialPortException {
        log.debug("Purging input ....");
        outboundStream.writeNAK();
        inboundStream.purgeStream();
        log.debug("Purge done");
    }

    public void reconnectPort() {
        int retryCount = 0;
        while (retryCount++ < configuration.getPortReconnectMaxCount()) {
            try {
                Thread.sleep(reconnectDelay(retryCount));
                serialPort.reconnect();
                inboundStream.purgeStream();
                return;
            } catch (SerialPortException e) {
                log.error("Port reconnection failed!", e);
            } catch (InterruptedException e) {
            }
        }

        try {
            serialPort.disconnect();
        } catch (SerialPortException e) {
            log.error("Shutting down port after reconnection failed unsuccessful!", e);
        }

        throw new FatalSerialException("Reconnection to serial port failed after %s retries!", retryCount);
    }

    private void enqueueRequest(SerialRequest serialRequest) {
        this.serialRequest = serialRequest;
        this.transmissionSuccess = false;
        this.retransmissionCounter = 0;
        this.retransmissionTime = currentTimeMillis();
        log.debug("ZWave request scheduled {}", serialRequest.getSerialCommand());
    }

    private boolean transmissionAwaiting() {
        return retransmissionTime <= System.currentTimeMillis();
    }

    private void receiveStage() throws RxTxException {
        IdleStageResult idleStageResult;
        do {
            idleStageResult = idleStageDoer.checkInbound();
        } while (idleStageResult != IdleStageResult.RESULT_SILENCE);
    }

    private void transmitStage() throws RxTxException {
        if (transmissionAwaiting()) {
            ImmutableBuffer frameData = serialRequest.getFrameData();
            frameData.rewind();
            RequestStageResult reqResult = requestStageDoer.sendRequest(frameData);
            boolean success = false;
            ResponseStageResult resResult = null;

            if (reqResult == RequestStageResult.RESULT_OK) {
                if (serialRequest.isResponseExpected()) {
                    byte commandCode = frameData.getByte(FRAME_OFFSET_COMMAND);
                    resResult = responseStageDoer.acquireResponse(commandCode);
                    success = resResult == ResponseStageResult.RESULT_OK;
                } else {
                    success = true;
                }
            }

            if (success) {
                transmissionSuccess = true;
                deactivateTransmission();
                log.debug("Transmission successful");
            } else {
                pursueRetransmission();
                log.warn("Transmission failed, request result: {}, response result: {}", reqResult, resResult == null ? "N/A" : resResult);
            }
        }
    }

    private void pursueRetransmission() {
        if (serialRequest.isRetransmissionDisabled() || ++retransmissionCounter > configuration.getRequestRetriesMaxCount()) {
            deactivateTransmission();
        } else {
            retransmissionTime = currentTimeMillis() + retransmissionDelay(retransmissionCounter);
        }
    }

    private long retransmissionDelay(int retryNumber) {
        return configuration.getRequestRetryDelayBias() + (configuration.getRequestRetryDelayFactor() * retryNumber);
    }

    private long reconnectDelay(int retryNumber) {
        return configuration.getPortReconnectDelayBias() + (configuration.getPortReconnectDelayFactor() * retryNumber);
    }

    private void deactivateTransmission() {
        this.retransmissionTime = Long.MAX_VALUE;
        this.outboundLock.release();
    }

    private void handleResponse(ViewBuffer frameView) {
        log.info("ZWaveResponse frame received: {}", bufferToString(frameView));
    }

    private void handleCallback(ViewBuffer frameView) {
        log.info("ZWaveCallback frame received: {}", bufferToString(frameView));
    }
}
