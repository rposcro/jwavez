package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.core.buffer.ByteBuffer;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class ChecksumUtilTest {

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("testCases")
    public void frameCrcBasedOnByteArray(byte[] bytes, int expectedCrc) {
        byte crc = ChecksumUtil.frameCrc(bytes);
        assertEquals(format("expected:<%02x> but was:<%02x>\n", (byte) expectedCrc, crc), (byte) expectedCrc, crc);
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("testCases")
    public void frameCrcBasedOnImmutableBuffer(byte[] bytes, int expectedCrc) {
        ImmutableBuffer buffer = ImmutableBuffer.overBuffer(bytes);

        byte crc = ChecksumUtil.frameCrc(buffer);
        assertEquals(format("expected:<%02x> but was:<%02x>\n", (byte) expectedCrc, crc), (byte) expectedCrc, crc);
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("testCases")
    public void crcBasedOnByteBuffer(byte[] bytes, int expectedCrc) {
        ByteBuffer buffer = new ByteBuffer(bytes.length, null);
        for (byte bt: bytes) {
            buffer.add(bt);
        }

        byte crc = ChecksumUtil.crc(buffer, 1, bytes.length - 2);
        assertEquals(format("expected:<%02x> but was:<%02x>\n", (byte) expectedCrc, crc), (byte) expectedCrc, crc);
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(new byte[] {0x01, 0x03, 0x00, 0x15, 0x00}, 0xe9),
                Arguments.of(new byte[] {0x01, 0x10, 0x01, 0x15, 0x5a, 0x2d, 0x57, 0x61, 0x76, 0x65, 0x20, 0x33, 0x2e, 0x39, 0x35, 0x00, 0x01, (byte) 0x99}, 0x99),
                Arguments.of(new byte[] {0x01, 0x03, 0x00, 0x56, 0x00}, 0xaa)
        );
    }
}
