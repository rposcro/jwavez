package com.rposcro.jwavez.tools.shell.commands.dongle;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.dongle.DongleNvmService;
import com.rposcro.jwavez.tools.shell.services.dongle.NvmFileRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.DONGLE)
public class NvmCommands {

    @Autowired
    private DongleNvmService dongleNvmService;

    @Autowired
    private NvmFileRenderer nvmFileRenderer;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ConsoleAccessor consoleAccessor;

    @Command(name = "nvm backup", description = "Backs up dongle NVM to a file", availabilityProvider = "dongleAvailability")
    public String nvmBackup()
    throws SerialException {
        byte[] dongleNvm = dongleNvmService.pullNvmDataFromDevice();
        consoleAccessor.flushLine(format("NVM content of size %s successfully read from dongle\n", dongleNvm.length));
        File filePath = backupFilePath();

        try(FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(nvmFileRenderer.renderNvmFileContent(dongleNvm));
        } catch (Exception e) {
            return "Failed to write NVM content to file: " + e.getMessage() + "\n";
        }

        return "NVM content flushed to file: " + filePath.getAbsolutePath() + "\n";
    }

    @Command(name = "nvm restore", description = "Restores dongle NVM from a file", availabilityProvider = "dongleAvailability")
    public String nvmRestore(@Argument(index = 0, description = "Path to hex file with NVM dump") String pathToFile)
    throws SerialException {
        return "NVM content not restored since it's placeholder still\n";
    }

    private File backupFilePath() {
        String filePath = format("nvm-%04x-%02x-%02x-%s.hex",
            shellContext.getDongleInformation().getDongleDeviceInformation().getManufacturerId(),
            shellContext.getDongleInformation().getDongleDeviceInformation().getChipType(),
            shellContext.getDongleInformation().getDongleDeviceInformation().getChipVersion(),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
        );
        return new File(filePath);
    }
}
