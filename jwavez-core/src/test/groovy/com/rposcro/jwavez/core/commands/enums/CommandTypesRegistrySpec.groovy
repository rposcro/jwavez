package com.rposcro.jwavez.core.commands.enums

import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException

import static com.rposcro.jwavez.core.enums.CommandClass.*;
import static com.rposcro.jwavez.core.commands.enums.AssociationCommandType.*;
import static com.rposcro.jwavez.core.commands.enums.MultiChannelCommandType.*;
import static com.rposcro.jwavez.core.commands.enums.SceneActivationCommandType.*;
import static com.rposcro.jwavez.core.commands.enums.SensorBinaryCommandType.*;
import static com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType.*;

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CommandTypesRegistrySpec extends Specification {

    def "correct type code returned {#commandType, #expectedCode}"() {
        when:
        def code = CommandTypesRegistry.codeOfType(commandType);

        then:
        code == (byte) expectedCode;

        where:
        commandType                     | expectedCode
        ASSOCIATION_GROUPINGS_GET       | 0x05
        ASSOCIATION_GROUPINGS_REPORT    | 0x06
        CONFIGURATION_BULK_SET          | 0x07
        CONFIGURATION_BULK_REPORT       | 0x09
        MULTI_CHANNEL_CAPABILITY_REPORT | 0x0a
        SCENE_ACTIVATION_SET            | 0x01
        SENSOR_BINARY_GET               | 0x02
    }

    def "successfully decodes type {#commandClass, #commandCode, #expectedType}"() {
        given:
        def commandTypesRegistry = new CommandTypesRegistry();

        when:
        def commandType = commandTypesRegistry.decodeCommandType(commandClass, (byte) commandCode);

        then:
        commandType == expectedType;

        where:
        commandClass                    | commandCode | expectedType
        CMD_CLASS_ASSOCIATION           | 0x01        | ASSOCIATION_SET
        CMD_CLASS_ASSOCIATION           | 0x04        | ASSOCIATION_REMOVE
        CMD_CLASS_ASSOCIATION           | 0x0b        | ASSOCIATION_SPECIFIC_GROUP_GET
        CMD_CLASS_MULTI_CHANNEL         | 0x06        | MULTI_INSTANCE_ENCAP
        CMD_CLASS_MULTI_CHANNEL         | 0x0c        | MULTI_CHANNEL_END_POINT_FIND_REPORT
        CMD_CLASS_SCENE_ACTIVATION      | 0x01        | SCENE_ACTIVATION_SET
        CMD_CLASS_SENSOR_BINARY         | 0x01        | SENSOR_BINARY_SUPPORTED_GET_SENSOR
        CMD_CLASS_SENSOR_BINARY         | 0x03        | SENSOR_BINARY_REPORT
        CMD_CLASS_CONFIGURATION         | 0x04        | CONFIGURATION_SET
        CMD_CLASS_CONFIGURATION         | 0x0b        | CONFIGURATION_NAME_REPORT
        CMD_CLASS_CONFIGURATION         | 0x01        | CONFIGURATION_DEFAULT_RESET
    }

    def "fails to decode unknown type {#commandClass, #commandCode}"() {
        given:
        def commandTypesRegistry = new CommandTypesRegistry();

        when:
        commandTypesRegistry.decodeCommandType(commandClass, (byte) commandCode);

        then:
        thrown CommandNotSupportedException

        where:
        commandClass                    | commandCode
        CMD_CLASS_ASSOCIATION           | 0xbb
        CMD_CLASS_MULTI_CHANNEL         | 0xbc
        CMD_CLASS_SCENE_ACTIVATION      | 0xbd
        CMD_CLASS_SENSOR_BINARY         | 0xbe
        CMD_CLASS_CONFIGURATION         | 0xbf
    }

    def "fails to decode type for unsupported command class {#commandClass, #commandCode}"() {
        given:
        def commandTypesRegistry = new CommandTypesRegistry();

        when:
        commandTypesRegistry.decodeCommandType(commandClass, (byte) commandCode);

        then:
        thrown CommandNotSupportedException

        where:
        commandClass                    | commandCode
        CMD_CLASS_AV_RENDERER_STATUS    | 0xbb
        CMD_CLASS_ENERGY_PRODUCTION     | 0xbb
    }
}
