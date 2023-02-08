package com.rposcro.jwavez.core.utils

import com.rposcro.jwavez.core.model.enums.BitLength
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

import static com.rposcro.jwavez.core.model.enums.BitLength.*

class BytesUtilSpec extends Specification {

    @Unroll
    def "bytes are written as MSB #value, #size"() {
        given:
        def buffer = new byte[size.bytesNumber];

        when:
        BytesUtil.writeMSBValue(buffer, 0, size, value);

        then:
        buffer == expected.stream().map({ num -> new Byte((byte) num) }).collect(Collectors.toList());

        where:
        value      | size          | expected
        5          | BIT_LENGTH_8  | [5]
        0xff       | BIT_LENGTH_8  | [0xff]
        0xff       | BIT_LENGTH_16 | [0, 0xff]
        0xff14     | BIT_LENGTH_16 | [0xff, 0x14]
        0xff       | BIT_LENGTH_32 | [0, 0, 0, 0xff]
        0xff14     | BIT_LENGTH_32 | [0, 0, 0xff, 0x14]
        0x7f14aabb | BIT_LENGTH_32 | [0x7f, 0x14, 0xaa, 0xbb]
    }
}
