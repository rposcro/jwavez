package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.utils.BeanPropertiesFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_MULTI_CHANNEL;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NetworkListeningService {

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private JwzSupportedCommandParser supportedCommandParser;

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    private final BeanPropertiesFormatter propertiesFormatter = new BeanPropertiesFormatter();
    private final InboundFrameParser serialFrameParser = new InboundFrameParser();
    private final Semaphore semaphore = new Semaphore(1);

    public void startListening() throws SerialException {
        if (!semaphore.tryAcquire()) {
            throw new IllegalStateException("Listening not available, semaphore busy!");
        }

        new Thread(() -> {
            try {
                serialCommunicationService.runGeneralAsynchronousFunction(controller -> {
                    semaphore.acquireUninterruptibly();
                    semaphore.release();
                    return null;
                }, this::treatSerialCallback);
            } catch (SerialException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stopListening() {
        console.flushLine("\nListener stop requested ...");
        semaphore.release();
    }

    private void treatSerialCallback(ImmutableBuffer frameBuffer) {
        console.flushLine("\nCallback frame received");
        console.flushLine(FramesUtil.asFineString(frameBuffer));

        try {
            ZWaveCallback callback = serialFrameParser.parseCallbackFrame(frameBuffer);
            console.flushLine(callback.asFineString());
            if (callback.getSerialCommand() == SerialCommand.APPLICATION_COMMAND_HANDLER) {
                treatApplicationCommandHandler(callback);
            }
        } catch (FrameParseException e) {
            console.flushLine("Failed to parse callback frame: " + e.getMessage());
        }

        console.flushLine("EOF");
    }

    private void treatApplicationCommandHandler(ZWaveCallback callback) {
        ApplicationCommandHandlerCallback appCmdCallback = (ApplicationCommandHandlerCallback) callback;
        ImmutableBuffer payload = ImmutableBuffer.overBuffer(appCmdCallback.getCommandPayload());

        if (supportedCommandParser.isCommandSupported(payload)) {
            ZWaveSupportedCommand command = supportedCommandParser.parseCommand(payload, appCmdCallback.getSourceNodeId());
            console.flushLine(command.asNiceString());
            if (isEncapsulation(command)) {
                treatMultiChannelEncapsulation((MultiChannelCommandEncapsulation) command);
            }
        } else {
            console.flushLine(String.format("Unsupported command class: %02x", payload.getByte(0)));
        }
    }

    private void treatMultiChannelEncapsulation(MultiChannelCommandEncapsulation encapsulation) {
        ImmutableBuffer payload = ImmutableBuffer.overBuffer(encapsulation.getEncapsulatedCommandPayload());

        if (supportedCommandParser.isCommandSupported(payload)) {
            console.flushLine(supportedCommandParser.parseCommand(payload, encapsulation.getSourceNodeId()).asNiceString());
        } else {
            console.flushLine(String.format("Unsupported encapsulated command class: %02x", payload.getByte(0)));
        }
    }

    private boolean isEncapsulation(ZWaveSupportedCommand command) {
        return command.getCommandClass() == CMD_CLASS_MULTI_CHANNEL
                && command.getCommandType() == MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP;
    }
}
