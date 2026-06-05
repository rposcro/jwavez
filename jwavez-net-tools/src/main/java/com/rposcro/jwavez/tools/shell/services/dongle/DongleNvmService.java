package com.rposcro.jwavez.tools.shell.services.dongle;

import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.responses.NvmBackupRestoreResponse;
import com.rposcro.jwavez.serial.model.NvmBackupRestoreResult;
import com.rposcro.jwavez.tools.shell.commands.exception.ServiceFlowBrokenException;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DongleNvmService {

    private final static byte CHUNK_LENGTH = 80;

    @Autowired
    private SerialCommunicationService controllerManager;

    @Autowired
    private SerialRequestFactory serialRequestFactory;

    @Autowired
    private ConsoleAccessor consoleAccessor;

    public byte[] pullNvmDataFromDevice() throws SerialException {
        int nvmLength = openNvm();
        byte[] buffer = new byte[nvmLength];
        int bytesRead = 0;
        NvmBackupRestoreResponse response = readNvmChunk(bytesRead);

        while (response.getBufferLength() > 0) {
            consoleAccessor.flushLine(String.format("vnmLength: %s, bytesRead: %s, responseLen: %s, responseOffset: %s",
                nvmLength, bytesRead, response.getBufferLength(), response.getBufferOffset()));
            System.arraycopy(response.getBuffer(), 0, buffer, bytesRead, response.getBufferLength());
            bytesRead += response.getBufferLength();
            response = readNvmChunk(bytesRead);
        }

        closeNvm();
        return buffer;
    }

    public byte[] readNvmDataFromFile(String pathToFile) {
        //TODO implement
        return new byte[0];
    }

    private NvmBackupRestoreResponse readNvmChunk(int offset) throws SerialException {
        SerialFunction<BasicSynchronousController, NvmBackupRestoreResponse> function = (controller) -> {
            NvmBackupRestoreResponse response = controller.requestResponseFlow(
                serialRequestFactory.deviceNvmRequestBuilder().createNvmBackupRestoreReadRequest(CHUNK_LENGTH, (short) offset));
            return response;
        };
        NvmBackupRestoreResponse response = controllerManager.runBasicSynchronousFunction(function);

        if (response.getResult() != NvmBackupRestoreResult.OK && response.getResult() != NvmBackupRestoreResult.EOF) {
            throw new ServiceFlowBrokenException("Backup NVM read request failed with result: " + response.getResult());
        }

        return response;
    }

    private int openNvm() throws SerialException {
        SerialFunction<BasicSynchronousController, NvmBackupRestoreResponse> function = (controller) -> {
            NvmBackupRestoreResponse response = controller.requestResponseFlow(
                serialRequestFactory.deviceNvmRequestBuilder().createNvmBackupRestoreOpenRequest());
            return response;
        };
        NvmBackupRestoreResponse response = controllerManager.runBasicSynchronousFunction(function);

        if (response.getResult() != NvmBackupRestoreResult.OK) {
            throw new ServiceFlowBrokenException("Backup NVM open request failed with result: " + response.getResult());
        }

        return response.getBufferOffset() + 1;
    }

    public void closeNvm() throws SerialException {
        SerialFunction<BasicSynchronousController, NvmBackupRestoreResponse> function = (controller) -> {
            NvmBackupRestoreResponse response = controller.requestResponseFlow(
                serialRequestFactory.deviceNvmRequestBuilder().createNvmBackupRestoreCloseRequest());
            return response;
        };
        NvmBackupRestoreResponse response = controllerManager.runBasicSynchronousFunction(function);

        if (response.getResult() != NvmBackupRestoreResult.OK) {
            throw new ServiceFlowBrokenException("Backup NVM close request failed with result: " + response.getResult());
        }
    }
}
