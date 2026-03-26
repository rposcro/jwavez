package com.rposcro.jwavez.tools.utils.text;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufferFormatterTest {

    private final static byte[] HEX_ARRAY;

    static {
        HEX_ARRAY = new byte[256];
        IntStream.range(0, 255)
            .forEachOrdered(i -> HEX_ARRAY[i] = (byte) i);
    };

    @Test
    public void testFormatBufferAsHexString_defaultFormat_emptyBuffer() {
        ByteBufferFormatter formatter = ByteBufferFormatter.builder().build();
        String result = formatter.formatBufferAsHexString(new byte[0]).toString();
        assertEquals("", result);
    }

    @Test
    public void testFormatBufferAsHexString_defaultFormat_singlePartLine() {
        ByteBufferFormatter formatter = ByteBufferFormatter.builder().build();
        byte[] buffer = Arrays.copyOfRange(HEX_ARRAY, 0x7d, 0x83);
        String result = formatter.formatBufferAsHexString(buffer).toString();
        assertEquals("7D7E7F808182", result);
    }

    @Test
    public void testFormatBufferAsHexString_defaultFormat_singleFullLine() {
        ByteBufferFormatter formatter = ByteBufferFormatter.builder().build();
        byte[] buffer = Arrays.copyOfRange(HEX_ARRAY, 0, 16);
        String result = formatter.formatBufferAsHexString(buffer).toString();
        assertEquals("000102030405060708090A0B0C0D0E0F", result);
    }

    @Test
    public void testFormatBufferAsHexString_withSmallLetters() {
        ByteBufferFormatter formatter = ByteBufferFormatter.builder()
            .byteFormat("%02x")
            .build();
        byte[] buffer = Arrays.copyOfRange(HEX_ARRAY, 8, 17);
        String result = formatter.formatBufferAsHexString(buffer).toString();
        assertEquals("08090a0b0c0d0e0f10", result);
    }

    @Test
    public void testFormatBufferAsHexString_withByteSeparator() {
        ByteBufferFormatter formatter = ByteBufferFormatter.builder()
            .byteSeparator(",")
            .build();
        byte[] buffer = Arrays.copyOfRange(HEX_ARRAY, 8, 17);
        String result = formatter.formatBufferAsHexString(buffer).toString();
        assertEquals("08,09,0A,0B,0C,0D,0E,0F,10", result);
    }

    @Test
    public void testFormatBufferAsHexString_withLineLengthAndLinePrefix() {
        int lineLength = 4;
        Function<Integer, String> linePrefixFunction =
            (lineNumber) -> String.format("%04X: ", lineNumber * lineLength);
        ByteBufferFormatter formatter = ByteBufferFormatter.builder()
            .lineLength(lineLength)
            .linePrefixFunction(linePrefixFunction)
            .build();
        byte[] buffer = Arrays.copyOfRange(HEX_ARRAY, 16, 32);
        String result = formatter.formatBufferAsHexString(buffer).toString();
        assertEquals("0000: 10111213\n0004: 14151617\n0008: 18191A1B\n000C: 1C1D1E1F", result);
    }

    @Test
    public void testFormatBufferAsHexString_withLineLengthAndLineSuffix() {
        int lineLength = 4;
        Function<Integer, String> lineSuffixFunction =
            (lineNumber) -> String.format(": %04X", lineNumber * lineLength);
        ByteBufferFormatter formatter = ByteBufferFormatter.builder()
            .lineLength(lineLength)
            .lineSuffixFunction(lineSuffixFunction)
            .build();
        byte[] buffer = Arrays.copyOfRange(HEX_ARRAY, 16, 22);
        String result = formatter.formatBufferAsHexString(buffer).toString();
        assertEquals("10111213: 0000\n1415: 0004", result);
    }
}
