package com.rposcro.jwavez.tools.shell.commands.dongle;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.BufferFormatter;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.DongleNvmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.standard.ShellComponent;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ShellComponent
@Command(group = CommandGroup.DONGLE)
public class NvmCommands {

    @Autowired
    private DongleNvmService dongleNvmService;

    @Autowired
    private BufferFormatter bufferFormatter;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ConsoleAccessor consoleAccessor;

    @Command(command = "nvm backup", description = "Backs up dongle NVM to a file")
    @CommandAvailability(provider = "dongleAvailability")
    public String nvmBackup()
    throws SerialException {
        byte[] dongleNvm = dongleNvmService.readNvmData();
        String formattedBuffer = bufferFormatter.formatBufferAsHexString(dongleNvm);
        consoleAccessor.flushLine("NVM content read from dongle: " + formattedBuffer + "\n");
        File filePath = backupFilePath();

        try(FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(formattedBuffer);
        } catch (Exception e) {
            return "Failed to write NVM content to file: " + e.getMessage() + "\n";
        }

        return "NVM content flushed to file: " + filePath.getAbsolutePath() + "\n";
    }

    private File backupFilePath() {
        String filePath = String.format("nvm-%04x-%02x-%02x-%s.hex",
            shellContext.getDongleInformation().getDongleDeviceInformation().getManufacturerId(),
            shellContext.getDongleInformation().getDongleDeviceInformation().getChipType(),
            shellContext.getDongleInformation().getDongleDeviceInformation().getChipVersion(),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
        );
        return new File(filePath);
    }
}
