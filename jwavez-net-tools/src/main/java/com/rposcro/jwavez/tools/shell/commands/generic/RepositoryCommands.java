package com.rposcro.jwavez.tools.shell.commands.generic;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

import java.io.IOException;

@ShellComponent
@Command(group = CommandGroup.GENERIC)
public class RepositoryCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private RepositoryService repositoryService;

    @Command(command = "repository", alias = "repo", description = "Shows current repository")
    public String showRepository() {
        if (shellContext.isRepositoryOpened()) {
            return "Current repository is " + shellContext.getRepositoryName();
        } else {
            return "No repository is currently opened";
        }
    }

    @Command(command = "repository create", alias = "repo create", description = "Creates new repository")
    @CommandAvailability(provider = "dongleAvailability")
    public String createRepository(
            @Option(longNames = "repository-name", required = true) String repositoryName
    ) throws IOException {
        if (repositoryService.repositoryExists(repositoryName)) {
            return "Repository " + repositoryName + " already exists, cannot override!";
        }

        repositoryService.createRepository(repositoryName);
        return "Repository " + repositoryName + " created";
    }

    @Command(command = "repository open", alias = "repo open", description = "Opens repository")
    public String openRepository(
            @Option(longNames = "repository-name", required = true) String repositoryName
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

    @Command(command = "repository persist", alias = "repo persist", description = "Persists repository")
    @CommandAvailability(provider = {"dongleAvailability", "repositoryAvailability"})
    public String persistRepository() throws IOException {
        repositoryService.persistRepository();
        return "Repository " + shellContext.getRepositoryName() + " persisted";
    }
}
