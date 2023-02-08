package com.rposcro.jwavez.core.commands

import com.rposcro.jwavez.core.JwzApplicationCommands
import com.rposcro.jwavez.core.commands.enums.AssociationCommandType
import com.rposcro.jwavez.core.commands.enums.BasicCommandType
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType
import com.rposcro.jwavez.core.commands.enums.SceneActivationCommandType
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport
import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet
import com.rposcro.jwavez.core.enums.CommandClass
import com.rposcro.jwavez.core.model.NodeId
import com.rposcro.jwavez.core.utils.ImmutableBuffer
import spock.lang.Specification
import spock.lang.Unroll

class SupportedCommandParserSpec extends Specification {

    def theParser;

    def setup() {
        theParser = new JwzApplicationCommands().supportedCommandParser();
    }

    @Unroll
    def "parses application command #commandClass type #commandType"() {
        given:
        def buffer = immutableBuffer(payload);

        when:
        ZWaveSupportedCommand command = theParser.parseCommand(buffer, new NodeId(33));

        then:
        command.getClass() == javaClass
        command.sourceNodeId.getId() == 33;
        command.commandClass == commandClass;
        command.commandType == commandType;

        where:
        payload                                        | javaClass                        | commandClass                            | commandType
        [0x20, 0x01, 0x00] as byte[]                   | BasicSet.class                   | CommandClass.CMD_CLASS_BASIC            | BasicCommandType.BASIC_SET
        [0x85, 0x03, 0x01, 0x01, 0x00, 0x00] as byte[] | AssociationReport.class          | CommandClass.CMD_CLASS_ASSOCIATION      | AssociationCommandType.ASSOCIATION_REPORT
        [0x85, 0x06, 0x01] as byte[]                   | AssociationGroupingsReport.class | CommandClass.CMD_CLASS_ASSOCIATION      | AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT
        [0x70, 0x06, 0x01, 0x01, 0xff] as byte[]       | ConfigurationReport.class        | CommandClass.CMD_CLASS_CONFIGURATION    | ConfigurationCommandType.CONFIGURATION_REPORT
        [0x2b, 0x01, 0x02, 0x80] as byte[]             | SceneActivationSet.class         | CommandClass.CMD_CLASS_SCENE_ACTIVATION | SceneActivationCommandType.SCENE_ACTIVATION_SET
        //[0x70, 0x09, 0x00, 0x00, 0x01, 0x00, 0x01, 0xaa] as byte[] | ConfigurationBulkReport.class | CommandClass.CMD_CLASS_CONFIGURATION | ConfigurationCommandType.CONFIGURATION_BULK_REPORT
    }

    def immutableBuffer(byte[] payload) {
        return new ImmutableBuffer(payload, 0, payload.length);
    }
}
