package com.rposcro.jwavez.core.utils

import com.rposcro.jwavez.core.enums.BasicDeviceClass
import com.rposcro.jwavez.core.enums.CommandClass
import com.rposcro.jwavez.core.enums.GenericDeviceClass
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
}
