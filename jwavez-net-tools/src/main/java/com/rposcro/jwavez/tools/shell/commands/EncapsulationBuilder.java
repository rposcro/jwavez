package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EncapsulationBuilder {

    private final static Pattern PARAMETER_PATTERN = Pattern.compile("[0-9]+,[0-9]+");

    @Autowired
    private MultiChannelCommandBuilder multiChannelCommandBuilder;

    public ZWaveControlledCommand encapsulateCommand(ZWaveControlledCommand command, String encapsulationParameter) {
        if (!PARAMETER_PATTERN.matcher(encapsulationParameter).matches()) {
            throw new IllegalArgumentException("Incorrect encapsulation parameter: '" + encapsulationParameter
            + "'. The pattern is '<sourceEndpointNumber>,<destinationEndpointNumber>', must match regex: [0-9]+,[0-9]+)");
        }
        int delimiterIndex = encapsulationParameter.indexOf(',');
        int sourceEndpoint = Integer.parseInt(encapsulationParameter.substring(0, delimiterIndex));
        int destinationEndpoint = Integer.parseInt(encapsulationParameter.substring(delimiterIndex + 1));
        return multiChannelCommandBuilder.v3().encapsulateCommand((byte) sourceEndpoint, (byte) destinationEndpoint, command);
    }
}
