package com.rposcro.jwavez.tools.shell.formatters;

import org.springframework.stereotype.Service;

@Service
public class BufferFormatter {

    private final static int LINE_LENGTH = 16;

    public String formatBufferAsHexString(byte[] buffer) {
        StringBuilder sb = new StringBuilder();
        int formatted = 0;

        while (formatted < buffer.length) {
            int lineEnd = Math.min(formatted + LINE_LENGTH, buffer.length);
            for (int i = formatted; i < lineEnd; i++) {
                if (i == lineEnd -1) {
                    sb.append(String.format("%02x", buffer[i]));
                } else {
                    sb.append(String.format("%02x ", buffer[i]));
                }
            }
            sb.append("\n");
            formatted += LINE_LENGTH;
        }

        return sb.toString().trim();
    }
}
