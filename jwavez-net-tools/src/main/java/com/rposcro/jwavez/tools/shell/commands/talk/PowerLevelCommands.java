package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.powerlevel.PowerLevelReport;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
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
public class PowerLevelCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private PowerLevelCommandBuilder powerLevelCommandBuilder;

    @ShellMethod(value = "Request power level report", key = {"powerlevel report", "pl report"})
    public String executePowerLevelReport(@ShellOption(value = {"--node-id", "-id"}) int nodeId) throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1().buildGetCommand();
        PowerLevelReport powerLevelReport = talkCommunicationService.requestTalk(nodeId, command, PowerLevelCommandType.POWER_LEVEL_REPORT);
        return String.format("Power level reported: 0x%02X, timeout is: %s[s]\n", powerLevelReport.getPowerLevel(), powerLevelReport.getTimeout());
    }

    @ShellMethod(value = "Power level set request", key = {"powerlevel set", "pl set"})
    public String executePowerLevelSet(
            @ShellOption(value = {"--node-id", "-id"}) int nodeId,
            @ShellOption(value = {"--power-level", "-pl"}) int powerLevel,
            @ShellOption(value = {"--level-timeout", "-lt"}) int powerLevelTimeout
    ) throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1()
                .buildSetCommand((byte) powerLevel, (byte) powerLevelTimeout);
        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s", PowerLevelCommandType.POWER_LEVEL_SET, nodeId);
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
