package com.rposcro.jwavez.utils

import com.rposcro.jwavez.enums.BasicDeviceClass
import com.rposcro.jwavez.enums.CommandClass
import com.rposcro.jwavez.enums.GenericDeviceClass
import AddNodeToNeworkStatus
import FrameCategory
import FrameType
import SerialCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class EncodableConstantsRegistrySpec extends Specification {

    def "correct code is returned #constant, #expectedCode"() {
        when:
        byte code = EncodableConstantsRegistry.codeOfConstant(constant);

        then:
        code == expectedCode;

        where:
        constant                                  | expectedCode
        BasicDeviceClass.BASIC_TYPE_ROUTING_SLAVE | 0x04
        CommandClass.CMD_CLASS_APPLICATION_STATUS | 0x22
        GenericDeviceClass.GENERIC_TYPE_METER     | 0x31
    }

    def "correct cpnstants is returned #constantClass, #code, #expectedConstant"() {

        when:
        def constant = EncodableConstantsRegistry.constantOfCode(constantClass, (byte) code);

        then:
        constant == expectedConstant;

        where:
        constantClass               | code | expectedConstant
        FrameCategory.class         | 0x06 | FrameCategory.ACK
        FrameType.class             | 0x00 | FrameType.REQ
        SerialCommand.class         | 0x15 | SerialCommand.GET_VERSION
        SerialCommand.class         | 0x49 | SerialCommand.APPLICATION_UPDATE
        AddNodeToNeworkStatus.class | 0x07 | AddNodeToNeworkStatus.ADD_NODE_STATUS_FAILED

    }
}
