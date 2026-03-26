package com.rposcro.jwavez.tools.shell.services.dongle;

import com.rposcro.jwavez.tools.shell.models.DongleNvmData;
import com.rposcro.jwavez.tools.shell.models.DongleNvmData.DongleNvmDataBuilder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static java.lang.String.format;

@Component
public class NvmFileParser {

    public static void main(String... args) throws Exception {
        NvmFileParser nvmFileParser = new NvmFileParser();
        DongleNvmData nvmData = nvmFileParser.parseNvmFileContent("./nvm-0115-05-00-2026-02-27T09:46:30.561133.hex");
        System.out.println(new StringBuilder()
            .append(format("ManufacturerId: %04X\n", nvmData.getManufacturerId()))
            .append(format("ProductType: %04X\n", nvmData.getProductType()))
            .append(format("ProductId: %04X\n", nvmData.getProductId()))
            .append(format("ApplicationVersion: %02X\n", nvmData.getAppVersion()))
            .append(format("ApplicationRevision: %02X\n", nvmData.getAppRevision()))
            .append(format("ChipType: %02X\n", nvmData.getChipType()))
            .append(format("ChipVersion: %02X\n", nvmData.getChipVersion()))
            .append(format("NvmSize: %04X\n", nvmData.getNvmBytes().length))
        );

        byte[] firstBytes = new byte[64];
        System.arraycopy(nvmData.getNvmBytes(), 0, firstBytes, 0, 64);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < firstBytes.length; i++) {
            builder.append(String.format("%02X", firstBytes[i]));
        }
        System.out.println(builder.toString());
    }

    public DongleNvmData parseNvmFileContent(String filePath) throws IOException {
        DongleNvmDataBuilder builder = DongleNvmData.builder();
        try(BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            readHeader(fileReader, builder);
            int nvmSize = (int) readPropertyLine(fileReader, "NvmSize");
            fileReader.readLine();
            byte[] bytes = readBytes(fileReader, nvmSize);

            System.out.println(nvmSize + " ?= " + bytes.length);

            builder.nvmBytes(bytes);
            long checksumFromFile = readPropertyLine(fileReader, "Checksum");
            long calculatedChecksum = calculateChecksum(bytes);

            System.out.println(checksumFromFile + " ?= " + calculatedChecksum);


//            if (checksumFromFile != calculatedChecksum) {
//                throw new StreamCorruptedException(format("Checksum mismatch: expected %08X but calculated %08X", checksumFromFile, calculatedChecksum));
//            }
        }
        return builder.build();
    }

    private void readHeader(BufferedReader fileReader, DongleNvmDataBuilder builder) throws IOException {
        builder.manufacturerId((int) readPropertyLine(fileReader, "ManufacturerId"));
        builder.productType((int) readPropertyLine(fileReader, "ProductType"));
        builder.productId((int) readPropertyLine(fileReader, "ProductId"));
        builder.appVersion((short) readPropertyLine(fileReader, "ApplicationVersion"));
        builder.appRevision((short) readPropertyLine(fileReader, "ApplicationRevision"));
        builder.chipType((short) readPropertyLine(fileReader, "ChipType"));
        builder.chipVersion((short) readPropertyLine(fileReader, "ChipVersion"));
    }

    private byte[] readBytes(BufferedReader fileReader, int bytesSize) throws IOException {
        byte[] bytes = new byte[bytesSize];
        int bytesRead = 0;
        String line;

        while(isNotEmpty(line = fileReader.readLine())) {
            int read = readBytes(line, bytes, bytesRead);
            bytesRead += read;
        }

        return bytes;
    }

    private int readBytes(String line, byte[] bytes, int offset) throws IOException {
        String[] split = line.split(":\\s{0,}");
        if (split.length != 2) {
            throw new InvalidObjectException(format("Incorrect byte line format: %s", line));
        }

        String byteString = split[1].trim();
        if (byteString.length() % 2 != 0) {
            throw new InvalidObjectException(format("Odd number of hex chars in byte line: %s", line));
        }

        for (int i = 0; i < byteString.length(); i += 2) {
            String byteHex = byteString.substring(i, i + 2);
            bytes[offset++] = (byte) Integer.parseInt(byteHex, 16);
        }

        return byteString.length() / 2;
    }

    private long readPropertyLine(BufferedReader fileReader, String headerName) throws IOException {
        String line;

        if (isEmpty(line = fileReader.readLine()) || !line.startsWith(headerName)) {
            throw new InvalidObjectException(format("Expected header line with name %s but got: %s", headerName, line));
        }

        String[] split = line.split(":\\s+");
        if (split.length != 2) {
            throw new InvalidObjectException(format("Incorrect header line format: %s", line));
        }
        return Long.parseLong(split[1].trim(), 16);
    }

    private long calculateChecksum(byte[] data) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        return checksum.getValue();
    }

    private boolean isEmpty(String line) {
        return line == null || line.trim().isEmpty();
    }

    private boolean isNotEmpty(String line) {
        return line != null && !line.trim().isEmpty();
    }
}
