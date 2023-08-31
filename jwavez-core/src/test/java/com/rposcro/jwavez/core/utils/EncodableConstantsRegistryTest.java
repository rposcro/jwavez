package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.classes.BasicDeviceClass;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncodableConstantsRegistryTest {

    @ParameterizedTest
    @MethodSource("testArguments")
    public void returnsCodeOfConstant(EncodableConstant constant, int expectedCode) {
        assertEquals((byte) expectedCode, EncodableConstantsRegistry.codeOfConstant(constant));
    }

    private static Stream<Arguments> testArguments() {
        return Stream.of(
                Arguments.of(BasicDeviceClass.BASIC_TYPE_ROUTING_SLAVE, 0x04),
                Arguments.of(CommandClass.CMD_CLASS_APPLICATION_STATUS, 0x22),
                Arguments.of(GenericDeviceClass.GENERIC_TYPE_METER, 0x31)
        );
    }
}
