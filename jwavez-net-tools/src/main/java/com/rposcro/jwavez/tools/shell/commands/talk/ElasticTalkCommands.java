package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.communication.ApplicationCommandResult;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_MULTI_CHANNEL;
import static java.lang.String.format;

@ShellComponent
@Command(group = CommandGroup.TALK)
public class ElasticTalkCommands {

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private JwzSupportedCommandParser supportedCommandParser;

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Command(command = "send", description = "Sends application command payload")
    @CommandAvailability(provider = "dongleAvailability")
    public String sendApplicationCommand(
            @Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId,
            @Option(longNames = "payload", required = true) String payload)
            throws SerialException {
        byte[] payloadBytes = parsePayload(payload);
        console.flushLine(format("Sending application command to " + nodeId));

        ApplicationCommandResult<ZWaveSupportedCommand> commandResult = talkCommunicationService.requestTalk(nodeId, payloadBytes);
        ZWaveSupportedCommand acquiredCommand = commandResult.getAcquiredSupportedCommand();

        console.flushLine(format("Serial response frame: %s",
                BuffersUtil.asString(commandResult.getSerialResponsePayload())));
        console.flushLine(format("Serial callback frame: %s",
                BuffersUtil.asString(commandResult.getSerialCallbackPayload())));
        console.flushLine(format("Serial callback: %s", commandResult.getSerialCallback().asFineString()));
        console.flushLine("Supported application command: " + acquiredCommand.asNiceString());

        if (isEncapsulation(acquiredCommand)) {
            try {
                console.flushLine("Encapsulated application command: " +
                        extractEncapsulatedCommand((MultiChannelCommandEncapsulation) acquiredCommand).asNiceString());
            } catch (Exception e) {
                console.flushLine("Encapsulated application command: Failed to parse");
            }
        }

        return "Done";
    }

    private boolean isEncapsulation(ZWaveSupportedCommand command) {
        return command.getCommandClass() == CMD_CLASS_MULTI_CHANNEL
                && command.getCommandType() == MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP;
    }

    private ZWaveSupportedCommand extractEncapsulatedCommand(MultiChannelCommandEncapsulation encapsulation) {
        ImmutableBuffer buffer = ImmutableBuffer.overBuffer(encapsulation.getEncapsulatedCommandPayload());
        return supportedCommandParser.parseCommand(buffer, encapsulation.getSourceNodeId());
    }

    private byte[] parsePayload(String payload) {
        String[] tokens = payload.split("\\s+");
        byte[] bytes = new byte[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            bytes[i] = (byte) Short.parseShort(tokens[i], 16);
        }

        return bytes;
    }
}
