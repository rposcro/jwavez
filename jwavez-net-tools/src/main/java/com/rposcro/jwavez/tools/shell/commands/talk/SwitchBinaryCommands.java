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
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.TALK)
public class SwitchBinaryCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    EncapsulationBuilder encapsulationBuilder;

    @Autowired
    private SwitchBinaryCommandBuilder switchBinaryCommandBuilder;

    @ShellMethod(value = "Request binary report", key = { "switchbinary report", "sb report" })
    public String executeBinaryReport(
            @ShellOption(value = { "--node-id", "-id" }) int nodeId,
            @ShellOption(value = { "--encapsulate", "-encap", "-ec" }, defaultValue = ShellOption.NULL) String encapsulationParameter
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

    @ShellMethod(value = "Binary set request", key = { "switchbinary set", "sb set" })
    public String executeBinarySet(
            @ShellOption(value = { "--node-id", "-id" }) int nodeId,
            @ShellOption(value = { "--binary-value", "-value" }) int binaryValue,
            @ShellOption(value = { "--encapsulate", "-encap", "-ec" }, defaultValue = ShellOption.NULL) String encapsulationParameter
    ) throws SerialException {
        ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) binaryValue);

        if (encapsulationParameter != null) {
            command = encapsulationBuilder.encapsulateCommand(command, encapsulationParameter);
        }

        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s\n", SwitchBinaryCommandType.BINARY_SWITCH_SET, nodeId);
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (ShellScope.TALK != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }
}
