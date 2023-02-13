package com.rposcro.jwavez.tools.cli.commands.network;

import com.rposcro.jwavez.core.JwzApplicationCommands;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.FrameType;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.InboundFrameValidator;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import com.rposcro.jwavez.tools.cli.commands.AbstractAsyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceBasedOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class ListenerCommand extends AbstractAsyncBasedCommand {

    private DefaultDeviceBasedOptions options;
    private InboundFrameValidator frameValidator;
    private InboundFrameParser frameParser;

    private final Map<SerialCommand, Function<ZWaveCallback, String>> formatters;

    {
        formatters = new HashMap<>();
        formatters.put(SerialCommand.APPLICATION_COMMAND_HANDLER, this::formatApplicationCommandHandler);
    }

    public ListenerCommand() {
        frameValidator = new InboundFrameValidator();
        frameParser = new InboundFrameParser();
    }

    @Override
    public void configure(String[] args) throws CommandOptionsException {
        options = new DefaultDeviceBasedOptions(args);
    }

    @Override
    public void execute() {
        System.out.println("Frame listener starting ...");
        ProcedureUtil.executeProcedure(this::startListening);
    }

    private void startListening() throws SerialException {
        connect(options).addCallbackInterceptor(this::intercept);
        System.out.println("Listening to inbound frames, exit with Ctrl+C\n");
        while (true) ;
    }

    private void intercept(ViewBuffer buffer) {
        System.out.println("Frame Data: " + BufferUtil.bufferToString(buffer));
        if (!frameValidator.validate(buffer)) {
            System.out.println("Invalid Frame!");
        } else {
            FrameType frameType = FrameUtil.type(buffer);
            SerialCommand serialCommand = FrameUtil.serialCommand(buffer);
            System.out.printf("%s : %s (%s)", frameType, serialCommand, serialCommand.getCode());

            if (frameType == FrameType.REQ) {
                System.out.println(formatCallback(buffer));
            }
            System.out.println("\n");
        }
    }

    private String formatCallback(ViewBuffer viewBuffer) {
        String visual = null;
        try {
            ZWaveCallback callback = frameParser.parseCallbackFrame(viewBuffer);
            SerialCommand serialCommand = callback.getSerialCommand();
            Function<ZWaveCallback, String> formatter = formatters.computeIfAbsent(serialCommand, sc -> this::formatAnyCommand);
            visual = formatter.apply(callback);
        } catch (FrameParseException e) {
            System.out.println("Failed to parse frame! " + e.getMessage());
        }

        return visual;
    }

    private String formatAnyCommand(ZWaveCallback callback) {
        return "";
    }

    private String formatApplicationCommandHandler(ZWaveCallback callback) {
        ApplicationCommandHandlerCallback achCallback = (ApplicationCommandHandlerCallback) callback;
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n  Source Node Id: " + achCallback.getSourceNodeId());
        buffer.append("\n  RxStatus: " + achCallback.getRxStatus().getFrameCast());

        try {
            ZWaveSupportedCommand command = new JwzApplicationCommands().supportedCommandParser().parseCommand(
                    ImmutableBuffer.overBuffer(achCallback.getCommandPayload()),
                    achCallback.getSourceNodeId());
            buffer.append("\n  Command Class: " + command.getCommandClass());
            buffer.append("\n  Command Type: " + command.getCommandType());
            classFields(command).forEach((name, value) -> buffer.append("\n  > " + name + ": " + value));
        } catch (CommandNotSupportedException e) {
            log.debug("", e);
            buffer.append("\nSupported application command parser not available!");
            buffer.append("\n  Command Class: " + e.getCommandClass());
            buffer.append("\n  Command Type: " + e.getCommandType());
        }

        return buffer.toString();
    }

    private Map<String, String> classFields(ZWaveSupportedCommand object) {
        Map<String, String> fieldsMap = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        Stream.of(fields).forEach(field -> {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                fieldsMap.put(field.getName(), value == null ? null : value.toString());
            } catch (IllegalAccessException e) {
                fieldsMap.put(field.getName(), "<" + e.getMessage() + ">");
            }
        });
        return fieldsMap;
    }
}
