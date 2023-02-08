package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
@ShellCommandGroup(CommandGroup.GENERIC)
public class RepositoryCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private RepositoryService repositoryService;

    @ShellMethod(value = "Show current repository", key={ "repository", "repo" })
    public String showRepository() {
        if (shellContext.isRepositoryOpened()) {
            return "Current repository is " + shellContext.getRepositoryName();
        } else {
            return "No repository is currently opened";
        }
    }

    @ShellMethod(value = "Create new repository", key={ "repository create", "repo create" })
    public String createRepository(
            @ShellOption(value = { "--repository-name", "-rn" }) String repositoryName
    ) throws IOException {
        if (repositoryService.repositoryExists(repositoryName)) {
            return "Repository " + repositoryName + " already exists, cannot override!";
        }

        repositoryService.createRepository(repositoryName);
        return "Repository " + repositoryName + " created";
    }

    @ShellMethod(value = "Open repository", key={ "repository open", "repo open" })
    public String openRepository(
            @ShellOption(value = { "--repository-name", "-rn" }) String repositoryName
    ) throws IOException {
        if (!shellContext.isDeviceReady()) {
            repositoryService.openRepositoryWithoutCheck(repositoryName);
            return "Repository " + repositoryName + " opened\nNo device is ready so persistence won't be possible!";
        } else if (repositoryService.openRepository(repositoryName)) {
            return "Repository " + repositoryName + " opened";
        } else {
            return "Repository " + repositoryName + " doesn't match current device, cannot open";
        }
    }

    @ShellMethod(value = "Persist repository", key={ "repository persist", "repo persist" })
    public String persistRepository() throws IOException {
        repositoryService.persistRepository();
        return "Repository " + shellContext.getRepositoryName() + " persisted";
    }

    @ShellMethodAvailability(value = { "repository create" })
    public Availability checkRepositoryCreateAvailability() {
        return shellContext.isDeviceReady() ?
                Availability.available() :
                Availability.unavailable("no ZWave dongle device is ready");
    }

    @ShellMethodAvailability(value = { "repository persist" })
    public Availability checkRepositoryPersistAvailability() {
        if (!shellContext.isDeviceReady()) {
            return Availability.unavailable("no ZWave dongle device is ready");
        }

        if (!shellContext.isRepositoryOpened()) {
            return Availability.unavailable("no repository is opened");
        }

        return Availability.available();
    }
}
