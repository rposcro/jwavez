package com.rposcro.jwavez.serial.probe.utils

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry
import com.rposcro.jwavez.serial.probe.frame.constants.AddNodeToNeworkStatus
import com.rposcro.jwavez.serial.probe.frame.constants.FrameCategory
import com.rposcro.jwavez.serial.probe.frame.constants.FrameType
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class EncodableConstantsRegistrySpec extends Specification {

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
