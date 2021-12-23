package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NetworkListeningService {

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private SerialControllerManager serialControllerManager;

    private final InboundFrameParser serialFrameParser = new InboundFrameParser();
    private final SupportedCommandParser supportedCommandParser = SupportedCommandParser.defaultParser();
    private final Semaphore semaphore = new Semaphore(1);

    public void startListening() throws SerialException {
        if (!semaphore.tryAcquire()) {
            throw new IllegalStateException("Listening not available, semaphore busy!");
        }

        new Thread(() -> {
            try {
                serialControllerManager.runGeneralAsynchronousFunction(controller -> {
                    semaphore.acquireUninterruptibly();
                    semaphore.release();
                    return null;
                }, this::handleSerialCallback);
            } catch(SerialException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stopListening() {
        console.flushLine("\nListener stop requested ...");
        semaphore.release();
    }

    private void handleSerialCallback(ViewBuffer viewBuffer) {
        console.flushLine("\nCallback frame received");
        console.flushLine(BufferUtil.bufferToString(viewBuffer));

        try {
            ZWaveCallback callback = serialFrameParser.parseCallbackFrame(viewBuffer);
            console.flushLine(String.format("%s (%02X)", callback.getSerialCommand(), callback.getSerialCommand().getCode()));
            if (callback.getSerialCommand() == SerialCommand.APPLICATION_COMMAND_HANDLER) {
                handleApplicationCommandHandler(callback);
            }
        } catch(FrameParseException e) {
            console.flushLine("Failed to parse callback frame: " + e.getMessage());
        }

        console.flushLine("EOF");
    }

    private void handleApplicationCommandHandler(ZWaveCallback callback) {
        ApplicationCommandHandlerCallback appCmdCallback = (ApplicationCommandHandlerCallback) callback;
        ImmutableBuffer payload = ImmutableBuffer.overBuffer(appCmdCallback.getCommandPayload());

        if (supportedCommandParser.isCommandSupported(payload)) {
            ZWaveSupportedCommand command = supportedCommandParser.parseCommand(payload, appCmdCallback.getSourceNodeId());
            console.flushLine(String.format("sourceNode: %02X, cmdClass: %s, cmdType: %s",
                    command.getSourceNodeId().getId(), command.getCommandClass(), command.getCommandType()));
        } else {
            console.flushLine(String.format("Unsupported command class: %02X", payload.getByte(0)));
        }
    }
}
