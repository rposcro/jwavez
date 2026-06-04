package com.rposcro.jwavez.tools.utils.text;

import lombok.Builder;

import java.util.function.Function;

@Builder
public class ByteBufferFormatter {

    @Builder.Default
    private final String byteFormat = "%02X";

    @Builder.Default
    private final int lineLength = 16;

    @Builder.Default
    private final String byteSeparator = "";

    @Builder.Default
    private final Function<Integer, String> linePrefixFunction = (lineNumber) -> "";

    @Builder.Default
    private final Function<Integer, String> lineSuffixFunction = (lineNumber) -> "";

    public StringBuilder formatBufferAsHexString(byte[] buffer) {
        StringBuilder sb = new StringBuilder();
        return formatBufferAsHexString(buffer, sb);
    }

    public StringBuilder formatBufferAsHexString(byte[] buffer, StringBuilder sb) {
        int formatted = 0;
        int lineNumber = 0;

        while (formatted < buffer.length) {
            sb.append(linePrefixFunction.apply(lineNumber));
            int lineEnd = Math.min(formatted + lineLength, buffer.length);
            for (int i = formatted; i < lineEnd; i++) {
                sb.append(String.format(byteFormat, buffer[i]));
                if (i < lineEnd - 1) {
                    sb.append(byteSeparator);
                }
                formatted++;
            }
            sb.append(lineSuffixFunction.apply(lineNumber));
            if (formatted < buffer.length) {
                sb.append("\n");
                lineNumber++;
            }
        }

        return sb;
    }
}
