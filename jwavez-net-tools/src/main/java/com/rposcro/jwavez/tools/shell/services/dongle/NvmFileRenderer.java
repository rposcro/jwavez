package com.rposcro.jwavez.tools.shell.services.dongle;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.DongleDeviceInformation;
import com.rposcro.jwavez.tools.utils.text.ByteBufferFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static java.lang.String.format;

@Component
public class NvmFileRenderer {

    private static final ByteBufferFormatter BUFFER_FORMATTER = createFormatter();

    @Autowired
    private JWaveZShellContext shellContext;

    public String renderNvmFileContent(byte[] nvmBytes) {
        StringBuilder contentBuilder = new StringBuilder();
        appendHeader(nvmBytes, contentBuilder).append("\n");
        appendNvmContent(nvmBytes, contentBuilder).append("\n");
        appendChecksum(nvmBytes, contentBuilder);
        return contentBuilder.toString();
    }

    private StringBuilder appendHeader(byte[] nvmBytes, StringBuilder sb) {
        DongleDeviceInformation deviceInformation = shellContext.getDongleInformation().getDongleDeviceInformation();
        return sb
            .append(format("ManufacturerId: %04X\n", deviceInformation.getManufacturerId()))
            .append(format("ProductType: %04X\n", deviceInformation.getProductType()))
            .append(format("ProductId: %04X\n", deviceInformation.getProductId()))
            .append(format("ApplicationVersion: %02X\n", deviceInformation.getAppVersion()))
            .append(format("ApplicationRevision: %02X\n", deviceInformation.getAppRevision()))
            .append(format("ChipType: %02X\n", deviceInformation.getChipType()))
            .append(format("ChipVersion: %02X\n", deviceInformation.getChipVersion()))
            .append(format("NvmSize: %04X\n", nvmBytes.length))
            ;
    }

    private StringBuilder appendNvmContent(byte[] nvmBytes, StringBuilder sb) {
        return BUFFER_FORMATTER.formatBufferAsHexString(nvmBytes, sb);
    }

    private StringBuilder appendChecksum(byte[] data, StringBuilder sb) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        sb.append(format("Checksum: %08X", checksum.getValue()));
        return sb;
    }

    private static ByteBufferFormatter createFormatter() {
        return ByteBufferFormatter.builder()
            .lineLength(32)
            .byteFormat("%02X")
            .byteSeparator("")
            .linePrefixFunction(lineNumber -> format("%04X:", lineNumber * 32))
            .build();
    }
}
