package com.rposcro.jwavez.tools.shell.formatters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BufferFormatterTest {

    @Test
    public void formatBufferAsHexString_whenBufferNotEqualsHexLength_shouldFormatBufferAsHexString() {
        BufferFormatter formatter = new BufferFormatter();
        byte[] buffer = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11};
        String expected = "01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10\n11";

        String result = formatter.formatBufferAsHexString(buffer);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void formatBufferAsHexString_whenBufferEqualsHexLength_shouldFormatBufferAsHexString() {
        BufferFormatter formatter = new BufferFormatter();
        byte[] buffer = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10};
        String expected = "01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10";

        String result = formatter.formatBufferAsHexString(buffer);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void formatBufferAsHexString_whenBufferIsEmpty_shouldFormatBufferAsHexString() {
        BufferFormatter formatter = new BufferFormatter();
        byte[] buffer = new byte[] {};
        String expected = "";

        String result = formatter.formatBufferAsHexString(buffer);

        Assertions.assertEquals(expected, result);
    }
}
