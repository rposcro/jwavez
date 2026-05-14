package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.commands.EncapsulationBuilder;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.TALK)
public class SwitchBinaryCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    EncapsulationBuilder encapsulationBuilder;

    @Autowired
    private SwitchBinaryCommandBuilder switchBinaryCommandBuilder;

    @Command(name = "switchbinary report", alias = "sb report", description = "Request binary report",
        availabilityProvider = "switchBinaryCommandsAvailability")
    public String executeBinaryReport(
            @Option(shortName = 'n', longName = "node-id", required = true) int nodeId,
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

    @Command(name = "switchbinary set", alias = "sb set", description = "Binary set request",
            availabilityProvider = "switchBinaryCommandsAvailability")
    public String executeBinarySet(
            @Option(shortName = 'n', longName = "node-id", required = true) int nodeId,
            @Option(shortName = 'v', longName = "value", required = true) int binaryValue,
            @Option(shortName = 'e', longName = "encapsulate") String encapsulationParameter
    ) throws SerialException {
        ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) binaryValue);

        if (encapsulationParameter != null && !encapsulationParameter.trim().isEmpty()) {
            command = encapsulationBuilder.encapsulateCommand(command, encapsulationParameter);
        }

        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s\n", SwitchBinaryCommandType.BINARY_SWITCH_SET, nodeId);
    }

    @Bean
    public AvailabilityProvider switchBinaryCommandsAvailability() {

        Availability availability;

        if (ShellScope.TALK != shellContext.getScopeContext().getScope()) {
            availability = Availability.unavailable("Command not available in current scope");
        } else {
            availability = shellContext.getDongleDevicePath() != null ?
                    Availability.available() :
                    Availability.unavailable("ZWave dongle device is not specified");
        }

        return AvailabilityProvider.of(availability);
    }
}
