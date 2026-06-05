package com.rposcro.jwavez.tools.shell.commands.generic;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

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
        availabilityProvider = "dongleAvailability")
    public String createRepository(
            @Argument(index = 0, description = "Name for new repository") String repositoryName
    ) throws IOException {
        if (repositoryService.repositoryExists(repositoryName)) {
            return "Repository " + repositoryName + " already exists, cannot override!";
        }

        repositoryService.createRepository(repositoryName);
        return "Repository " + repositoryName + " created";
    }

    @Command(name = "repository open", alias = "repo open", description = "Opens repository")
    public String openRepository(
            @Argument(index = 0, description = "Repository to open") String repositoryName
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
            availabilityProvider = "repositoryAvailability")
    public String persistRepository() throws IOException {
        repositoryService.persistRepository();
        return "Repository " + shellContext.getRepositoryName() + " persisted";
    }
}
