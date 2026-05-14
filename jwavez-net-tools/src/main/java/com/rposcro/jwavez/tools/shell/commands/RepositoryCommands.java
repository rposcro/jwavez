package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

import java.io.IOException;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.GENERIC)
public class RepositoryCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private RepositoryService repositoryService;

    @Command(name = "repository", alias = "repo", description = "Show current repository")
    public String showRepository() {
        if (shellContext.isRepositoryOpened()) {
            return "Current repository is " + shellContext.getRepositoryName();
        } else {
            return "No repository is currently opened";
        }
    }

    @Command(name = "repository create", alias = "repo create", description = "Create new repository",
        availabilityProvider = "repositoryCreateAvailability")
    public String createRepository(
            @Option(shortName = 'r', longName = "repository-name", required = true) String repositoryName
    ) throws IOException {
        if (repositoryService.repositoryExists(repositoryName)) {
            return "Repository " + repositoryName + " already exists, cannot override!";
        }

        repositoryService.createRepository(repositoryName);
        return "Repository " + repositoryName + " created";
    }

    @Command(name = "repository open", alias = "repo open", description = "Opens repository")
    public String openRepository(
            @Option(shortName = 'r', longName = "repository-name", required = true) String repositoryName
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

    @Command(name = "repository persist", alias = "repo persist", description = "Persists repository",
            availabilityProvider = "respositoryPersistPersistAvailability")
    public String persistRepository() throws IOException {
        repositoryService.persistRepository();
        return "Repository " + shellContext.getRepositoryName() + " persisted";
    }

    @Bean
    public AvailabilityProvider repositoryCreateAvailability() {
        return AvailabilityProvider.of(shellContext.isDeviceReady() ?
                Availability.available() :
                Availability.unavailable("no ZWave dongle device is ready"));
    }

    @Bean
    public AvailabilityProvider respositoryPersistPersistAvailability() {
        Availability availability;

        if (!shellContext.isDeviceReady()) {
            availability = Availability.unavailable("no ZWave dongle device is ready");
        } else if (!shellContext.isRepositoryOpened()) {
            availability = Availability.unavailable("no repository is opened");
        } else {
            availability =  Availability.available();
        }

        return AvailabilityProvider.of(availability);
    }
}
