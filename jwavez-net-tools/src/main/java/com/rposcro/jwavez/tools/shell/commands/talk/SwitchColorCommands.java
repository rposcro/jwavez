package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorSupportedReport;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.TALK)
public class SwitchColorCommands {

    private final static Pattern COLOR_PATTERN = Pattern.compile("(?:[a-fA-F0-9]{2})+");

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private SwitchColorCommandBuilder switchColorCommandBuilder;

    @Command(name = "switch-color report", alias = "sc report", description = "Request color report",
        availabilityProvider = "dongleAvailability")
    public String executeColorReport(
            @Argument(index = 0, description = "Node id where the switch color report request should be sent to") int nodeId)
            throws SerialException {
        ZWaveControlledCommand command = switchColorCommandBuilder.v1().buildSupportedGetCommand();
        SwitchColorSupportedReport supportedReport = talkCommunicationService.requestTalk(nodeId, command, SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT);

        List<ColorComponent> colorComponents = supportedReport.getColorComponents();
        console.flushLine("Color components found for node %s: %s".formatted(nodeId,
            colorComponents.stream().map(ColorComponent::toString).collect(Collectors.joining(", "))));

        StringBuffer message = new StringBuffer("Color components report for node " + nodeId);

        for (ColorComponent colorComponent : colorComponents) {
            command = switchColorCommandBuilder.v1().buildGetCommand(colorComponent.getCode());
            SwitchColorReport colorReport = talkCommunicationService.requestTalk(nodeId, command, SwitchColorCommandType.SWITCH_COLOR_REPORT);
            message.append(format("\n  %s (%s): 0x%02X", colorComponent.name(), colorComponent.getCode(), colorReport.getCurrentValue()));
        }

        return message.toString() + "\n";
    }

    @Command(name = "switch-color set", alias = "sc set", description = "Send color set request",
        availabilityProvider = "dongleAvailability")
    public String executeColorSet(
            @Argument(index = 0, description = "Node id where the color should be set") int nodeId,
            @Argument(index = 1, description = "Color mode to be used { RGB, RGBWW, RGBWC }") String colorMode,
            @Argument(index = 2, description = "Color value to be sent") String colorValue
    ) throws SerialException {
        String errorMessage = validateArguments(colorMode, colorValue);
        if (errorMessage != null) {
            return errorMessage + "\n";
        }

        ColorMode mode = ColorMode.valueOf(colorMode.toUpperCase());
        byte[] componentsFrame = constructComponentsFrame(mode, colorValue);
        ZWaveControlledCommand command = switchColorCommandBuilder.v2().buildSetCommand((byte) 0, componentsFrame);
        talkCommunicationService.sendCommand(nodeId, command);

        return format("Command %s successfully sent to node %s", SwitchColorCommandType.SWITCH_COLOR_SET, nodeId);
    }

    private byte[] constructComponentsFrame(ColorMode mode, String colorValue) {
        byte[] frame = new byte[colorValue.length()];
        byte[] components = mode.components;

        for (int i = 0; i < colorValue.length() / 2; i++) {
            frame[i * 2] = components[i];
            frame[i * 2 + 1] = (byte) Integer.parseInt(colorValue.substring(i * 2, i * 2 + 2), 16);
        }

        return frame;
    }

    private String validateArguments(String colorMode, String colorValue) {
        try {
            ColorMode mode = ColorMode.valueOf(colorMode.toUpperCase());
            if (!COLOR_PATTERN.matcher(colorValue).matches()) {
                return "Wrong color value, use hexadecimal notation of correct length";
            }
            if (colorValue.length() / 2 != mode.componentsCount) {
                return "Wrong color value, found " + (colorValue.length() / 2)
                        + " color components but " + mode.componentsCount + " are required";
            }
            return null;
        } catch (IllegalArgumentException e) {
            return "Unknown color mode " + colorMode + ", available modes: "
                    + Arrays.stream(ColorMode.values()).map(ColorMode::name).collect(Collectors.joining(", "));
        }
    }

    private final static byte WARM_WHITE = 0;
    private final static byte COLD_WHITE = 1;
    private final static byte RED = 2;
    private final static byte GREEN = 3;
    private final static byte BLUE = 4;

    private enum ColorMode {
        RGB(3, RED, GREEN, BLUE),
        RGBWW(4, RED, GREEN, BLUE, WARM_WHITE),
        RGBWC(4, RED, GREEN, BLUE, COLD_WHITE);

        private int componentsCount;
        private byte[] components;

        ColorMode(int componentsCount, byte... components) {
            this.componentsCount = componentsCount;
            this.components = components;
        }
    }
}
