package com.rposcro.jwavez.core.commands;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.core.commands.types.AssociationCommandType;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwzSupportedCommandParserTest {

    @ParameterizedTest
    @MethodSource("testArguments")
    public void commandsAreParsed(
            byte[] payload, Class<? extends CommandType> expectedClass, CommandClass expectedCommandClass, CommandType expectedCommandType) {
        JwzSupportedCommandParser parser = JwzSupportedCommandParser.defaultParser();
        ZWaveSupportedCommand<?> command = parser.parseCommand(ImmutableBuffer.overBuffer(payload), NodeId.forId(10));

        assertEquals(expectedClass, command.getClass());
        assertEquals(expectedCommandClass, command.getCommandClass());
        assertEquals(expectedCommandType, command.getCommandType());
    }

    private static Stream<Arguments> testArguments() {
        return Stream.of(
            Arguments.of(new byte[] {0x20, 0x01, 0x00}, BasicSet.class, CommandClass.CMD_CLASS_BASIC, BasicCommandType.BASIC_SET),
            Arguments.of(new byte[] {(byte) 0x85, 0x03, 0x01, 0x01, 0x00, 0x00}, AssociationReport.class, CommandClass.CMD_CLASS_ASSOCIATION, AssociationCommandType.ASSOCIATION_REPORT),
            Arguments.of(new byte[] {(byte) 0x85, 0x06, 0x01}, AssociationGroupingsReport.class, CommandClass.CMD_CLASS_ASSOCIATION, AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT),
            Arguments.of(new byte[] {0x70, 0x06, 0x01, 0x01, (byte) 0xff}, ConfigurationReport.class, CommandClass.CMD_CLASS_CONFIGURATION, ConfigurationCommandType.CONFIGURATION_REPORT),
            Arguments.of(new byte[] {0x2b, 0x01, 0x02, (byte) 0x80}, SceneActivationSet.class, CommandClass.CMD_CLASS_SCENE_ACTIVATION, SceneActivationCommandType.SCENE_ACTIVATION_SET)
        );
    }
}
