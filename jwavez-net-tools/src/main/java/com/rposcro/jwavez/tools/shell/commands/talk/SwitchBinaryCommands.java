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
import com.rposcro.jwavez.tools.utils.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.TALK)
public class SwitchBinaryCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    EncapsulationBuilder encapsulationBuilder;

    @Autowired
    private SwitchBinaryCommandBuilder switchBinaryCommandBuilder;

    @Command(name = "switch-binary report", alias = "sb report", description = "Request binary report",
        availabilityProvider = "dongleAvailability")
    public String executeBinaryReport(
            @Argument(index = 0, description = "Node id where the switch binary report request should be sent") int nodeId,
            @Option(shortName = 'e', longName = "--encapsulate") String encapsulationParameter
    ) throws SerialException {
        ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildGetCommand();
        short reportValue;

        if (encapsulationParameter == null || encapsulationParameter.trim().isEmpty()) {
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

    @Command(name = "switch-binary set", alias = "sb set", description = "Binary set request",
            availabilityProvider = "dongleAvailability")
    public String executeBinarySet(
            @Argument(index = 0, description = "Node id where the switch binary set request should be sent to") int nodeId,
            @Argument(index = 1, description = "Value for the binary switch") int binaryValue,
            @Option(shortName = 'e', longName = "encapsulate") String encapsulationParameter
    ) throws SerialException {
        ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) binaryValue);

        if (!Strings.isNullOrEmpty(encapsulationParameter)) {
            command = encapsulationBuilder.encapsulateCommand(command, encapsulationParameter);
        }

        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s\n", SwitchBinaryCommandType.BINARY_SWITCH_SET, nodeId);
    }
}
