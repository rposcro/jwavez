package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_ASSOCIATION;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_AV_RENDERER_STATUS;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_CONFIGURATION;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_MULTI_CHANNEL;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SCENE_ACTIVATION;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SENSOR_BINARY;
import static com.rposcro.jwavez.core.commands.types.AssociationCommandType.ASSOCIATION_GROUPINGS_GET;
import static com.rposcro.jwavez.core.commands.types.AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT;
import static com.rposcro.jwavez.core.commands.types.AssociationCommandType.ASSOCIATION_REMOVE;
import static com.rposcro.jwavez.core.commands.types.AssociationCommandType.ASSOCIATION_SET;
import static com.rposcro.jwavez.core.commands.types.AssociationCommandType.ASSOCIATION_SPECIFIC_GROUP_GET;
import static com.rposcro.jwavez.core.commands.types.ConfigurationCommandType.CONFIGURATION_BULK_REPORT;
import static com.rposcro.jwavez.core.commands.types.ConfigurationCommandType.CONFIGURATION_BULK_SET;
import static com.rposcro.jwavez.core.commands.types.ConfigurationCommandType.CONFIGURATION_DEFAULT_RESET;
import static com.rposcro.jwavez.core.commands.types.ConfigurationCommandType.CONFIGURATION_NAME_REPORT;
import static com.rposcro.jwavez.core.commands.types.ConfigurationCommandType.CONFIGURATION_SET;
import static com.rposcro.jwavez.core.commands.types.MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_REPORT;
import static com.rposcro.jwavez.core.commands.types.MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND_REPORT;
import static com.rposcro.jwavez.core.commands.types.MultiChannelCommandType.MULTI_INSTANCE_ENCAP;
import static com.rposcro.jwavez.core.commands.types.SceneActivationCommandType.SCENE_ACTIVATION_SET;
import static com.rposcro.jwavez.core.commands.types.SensorBinaryCommandType.SENSOR_BINARY_GET;
import static com.rposcro.jwavez.core.commands.types.SensorBinaryCommandType.SENSOR_BINARY_REPORT;
import static com.rposcro.jwavez.core.commands.types.SensorBinaryCommandType.SENSOR_BINARY_SUPPORTED_GET_SENSOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTypesRegistryTest {

    @ParameterizedTest
    @MethodSource("testTypeCodesArguments")
    public void returnsCodeOfCommandType(CommandType commandType, int expectedCode) {
        assertEquals((byte) expectedCode, CommandTypesRegistry.codeOfType(commandType));
    }

    @ParameterizedTest
    @MethodSource("testDecodeTypeArguments")
    public void returnsCodeOfCommandType(CommandClass commandClass, int code, CommandType expectedCommandType) {
        assertEquals(expectedCommandType, CommandTypesRegistry.decodeCommandType(commandClass, (byte) code));
    }

    @Test
    public void failsToDecodeUnknownCommandType() {
        assertThrows(CommandNotSupportedException.class, () ->
                CommandTypesRegistry.decodeCommandType(CMD_CLASS_ASSOCIATION, (byte) 0xbb));
    }

    @Test
    public void failsToDecodeUnknownCommandClas() {
        assertThrows(CommandNotSupportedException.class, () ->
                CommandTypesRegistry.decodeCommandType(CMD_CLASS_AV_RENDERER_STATUS, (byte) 0xbb));
    }

    private static Stream<Arguments> testTypeCodesArguments() {
        return Stream.of(
            Arguments.of(ASSOCIATION_GROUPINGS_GET, 0x05),
            Arguments.of(ASSOCIATION_GROUPINGS_REPORT, 0x06),
            Arguments.of(CONFIGURATION_BULK_SET, 0x07),
            Arguments.of(CONFIGURATION_BULK_REPORT, 0x09),
            Arguments.of(MULTI_CHANNEL_CAPABILITY_REPORT, 0x0a),
            Arguments.of(SCENE_ACTIVATION_SET, 0x01),
            Arguments.of(SENSOR_BINARY_GET, 0x02)
        );
    }
    
    private static Stream<Arguments> testDecodeTypeArguments() {
        return Stream.of(
            Arguments.of(CMD_CLASS_ASSOCIATION, 0x01, ASSOCIATION_SET),
            Arguments.of(CMD_CLASS_ASSOCIATION, 0x04, ASSOCIATION_REMOVE),
            Arguments.of(CMD_CLASS_ASSOCIATION, 0x0b, ASSOCIATION_SPECIFIC_GROUP_GET),
            Arguments.of(CMD_CLASS_MULTI_CHANNEL, 0x06, MULTI_INSTANCE_ENCAP),
            Arguments.of(CMD_CLASS_MULTI_CHANNEL, 0x0c, MULTI_CHANNEL_END_POINT_FIND_REPORT),
            Arguments.of(CMD_CLASS_SCENE_ACTIVATION, 0x01, SCENE_ACTIVATION_SET),
            Arguments.of(CMD_CLASS_SENSOR_BINARY, 0x01, SENSOR_BINARY_SUPPORTED_GET_SENSOR),
            Arguments.of(CMD_CLASS_SENSOR_BINARY, 0x03, SENSOR_BINARY_REPORT),
            Arguments.of(CMD_CLASS_CONFIGURATION, 0x04, CONFIGURATION_SET),
            Arguments.of(CMD_CLASS_CONFIGURATION, 0x0b, CONFIGURATION_NAME_REPORT),
            Arguments.of(CMD_CLASS_CONFIGURATION, 0x01, CONFIGURATION_DEFAULT_RESET)
        );
    }
}
