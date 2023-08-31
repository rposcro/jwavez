package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.core.utils.ObjectsUtil.orDefault;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static java.lang.System.currentTimeMillis;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.StreamTimeoutException;
import com.rposcro.jwavez.serial.exceptions.StreamMalformedException;
import com.rposcro.jwavez.serial.exceptions.StreamFlowException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.exceptions.StreamException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;

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
            deactivateTransmission();
        }
    }

    public void runSingleCycle() throws RxTxException {
        try {
            do {
                while (receiveStage() > 0);
                Thread.sleep(configuration.getRouterPollDelay());
            } while(receiveStage() > 0);
            transmitStage();
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
        log.debug("Rx {}: scheduled {}", serialRequest.getId(), serialRequest.getSerialCommand());
    }

    private boolean transmissionAwaiting() {
        return retransmissionTime <= System.currentTimeMillis();
    }

    private int receiveStage() throws RxTxException {
        int inboundsRead = -1;
        IdleStageResult idleStageResult;
        do {
            inboundsRead++;
            idleStageResult = idleStageDoer.checkInbound();
        } while (idleStageResult != IdleStageResult.RESULT_SILENCE);
        return inboundsRead;
    }

    private void transmitStage() throws RxTxException {
        if (transmissionAwaiting()) {
            log.debug("Rx {}: transmission attempt #{}", serialRequest.getId(), retransmissionCounter);
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
                log.debug("Rx {}: transmission successful", serialRequest.getId());
                deactivateTransmission();
            } else {
                log.warn("Rx {}: transmission failed, request result: {}, response result: {}",
                        serialRequest.getId(), reqResult, resResult == null ? "N/A" : resResult);
                pursueRetransmission();
            }
        }
    }

    private void pursueRetransmission() {
        if (serialRequest.isRetransmissionDisabled() || ++retransmissionCounter > configuration.getRequestRetriesMaxCount()) {
            log.debug("Rx {}: retransmission voided", serialRequest.getId());
            deactivateTransmission();
        } else {
            retransmissionTime = currentTimeMillis() + retransmissionDelay(retransmissionCounter);
            log.debug("Rx {}: retransmission {} scheduled", retransmissionCounter, serialRequest.getId());
        }
    }

    private long retransmissionDelay(int retryNumber) {
        return configuration.getRequestRetryDelayBias() + (configuration.getRequestRetryDelayFactor() * retryNumber);
    }

    private long reconnectDelay(int retryNumber) {
        return configuration.getPortReconnectDelayBias() + (configuration.getPortReconnectDelayFactor() * retryNumber);
    }

    private void deactivateTransmission() {
        if (serialRequest != null) {
            log.debug("Rx {}: retransmission deactivated", serialRequest.getId());
            this.serialRequest.getFrameData().dispose();
            this.serialRequest = null;
        }

        this.retransmissionTime = Long.MAX_VALUE;
        this.outboundLock.release();
    }

    private void handleResponse(ImmutableBuffer frameBuffer) {
        log.info("ZWaveResponse frame received: {}", BuffersUtil.asString(frameBuffer));
    }

    private void handleCallback(ImmutableBuffer frameBuffer) {
        log.info("ZWaveCallback frame received: {}", BuffersUtil.asString(frameBuffer));
    }
}
