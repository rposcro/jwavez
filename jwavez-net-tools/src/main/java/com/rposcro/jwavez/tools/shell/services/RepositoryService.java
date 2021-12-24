package com.rposcro.jwavez.tools.shell.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.models.RepositoryFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class RepositoryService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private DongleInformationService dongleInformationService;

    public boolean repositoryExists(String repositoryName) {
        return repositoryFile(repositoryName).exists();
    }

    public void createRepository(String repositoryName) throws IOException {
        File repositoryFile = repositoryFile(repositoryName);
        RepositoryFile repositoryModel = RepositoryFile.builder()
                .repositoryName(repositoryName)
                .relatedDongleDevice(shellContext.getDongleInformation().getDongleDeviceInformation())
                .nodes(Collections.emptyList())
                .build();
        objectMapper.writeValue(repositoryFile, repositoryModel);
    }

    public boolean openRepository(String repositoryName) throws IOException {
        File repositoryFile = repositoryFile(repositoryName);
        RepositoryFile repositoryModel = objectMapper.readValue(repositoryFile, RepositoryFile.class);

        if (dongleInformationService.matchesCurrentDongle(repositoryModel.getRelatedDongleDevice())) {
            shellContext.setRepositoryName(repositoryName);
            nodeInformationCache.clearCache();
            repositoryModel.getNodes().stream().forEach(
                    nodeInformation -> nodeInformationCache.cacheNodeInformation(nodeInformation)
            );
            return true;
        } else {
            return false;
        }
    }

    public void openRepositoryWithoutCheck(String repositoryName) throws IOException {
        File repositoryFile = repositoryFile(repositoryName);
        RepositoryFile repositoryModel = objectMapper.readValue(repositoryFile, RepositoryFile.class);
        shellContext.setRepositoryName(repositoryName);
        nodeInformationCache.clearCache();
        repositoryModel.getNodes().stream().forEach(
                nodeInformation -> nodeInformationCache.cacheNodeInformation(nodeInformation)
        );
    }

    public void detachRepository() {
        nodeInformationCache.clearCache();
        shellContext.setRepositoryName(null);
    }

    public void persistRepository() throws IOException {
        if (shellContext.getRepositoryName() == null) {
            throw new IllegalStateException("Persistence not possible without repository opened at the time");
        }

        if (shellContext.getDongleInformation() == null) {
            throw new IllegalStateException("Persistence not possible without active dongle");
        }

        RepositoryFile repositoryModel = RepositoryFile.builder()
                .relatedDongleDevice(shellContext.getDongleInformation().getDongleDeviceInformation())
                .nodes(nodeInformationCache.getOrderedNodeList())
                .build();
        objectMapper.writeValue(repositoryFile(shellContext.getRepositoryName()), repositoryModel);
    }

    private File repositoryFile(String repositoryName) {
        return new File(shellContext.getWorkspaceDir(), repositoryName + ".json");
    }
}
