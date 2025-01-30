package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.commands.EncapsulationBuilder;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@Command(group = CommandGroup.TALK)
public class SwitchBinaryCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    EncapsulationBuilder encapsulationBuilder;

    @Autowired
    private SwitchBinaryCommandBuilder switchBinaryCommandBuilder;

    @Command(command = "switch-binary report", alias = "sb report", description = "Request binary report")
    @CommandAvailability(provider = "dongleAvailability")
    public String executeBinaryReport(
            @Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId,
            @Option(longNames = "encapsulation", shortNames = 'e') String encapsulationParameter
    ) throws SerialException {
        ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildGetCommand();
        short reportValue;

        if (encapsulationParameter == null) {
            BinarySwitchReport binaryReport = talkCommunicationService.requestTalk(nodeId, command, SwitchBinaryCommandType.BINARY_SWITCH_REPORT);
            reportValue = binaryReport.getValue();
        } else {
            command = encapsulationBuilder.encapsulateCommand(command, encapsulationParameter);
            MultiChannelCommandEncapsulation encapsulation = talkCommunicationService.requestTalk(
                    nodeId, command, MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP);
            reportValue = (short) (encapsulation.getEncapsulatedCommandPayload()[0] & 0xff);
        }

        return String.format("Binary value reported: 0x%02X\n", reportValue);
    }

    @Command(command = "switch-binary set", alias = "sb set", description = "Binary set request")
    @CommandAvailability(provider = "dongleAvailability")
    public String executeBinarySet(
            @Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId,
            @Option(longNames = "value", shortNames = 'w', required = true) int binaryValue,
            @Option(longNames = "encapsulation", shortNames = 'e') String encapsulationParameter
    ) throws SerialException {
        ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) binaryValue);

        if (encapsulationParameter != null) {
            command = encapsulationBuilder.encapsulateCommand(command, encapsulationParameter);
        }

        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s\n", SwitchBinaryCommandType.BINARY_SWITCH_SET, nodeId);
    }
}
