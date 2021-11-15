package com.rposcro.jwavez.tools.shell.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Service
@Scope(SCOPE_SINGLETON)
public class CachePersistenceService {

    @Autowired
    private ObjectMapper objectMapper;

    private String baseDir;
    private File nodeDetailsFile;

    @PostConstruct
    public void setupService() {
        try {
            this.baseDir = System.getProperty("user.home");
            if (baseDir == null) {
                log.warn("User home dir not found, cache persistence not available!");
            } else {
                nodeDetailsFile = new File(new File(new File(baseDir, ".config"), "jwavez"), "node-details.json");
                if (!nodeDetailsFile.exists()) {
                    nodeDetailsFile.getParentFile().mkdirs();
                    nodeDetailsFile.createNewFile();
                    objectMapper.writeValue(nodeDetailsFile, Collections.emptyList());
                }
            }
        } catch(IOException e) {
            log.warn("Failed to create cache file, persistence not available!", e);
        }
    }

    public void persistNodesDetails(Collection<NodeInformation> nodeDetailsCollection) {
        if (isPersistenceAvailable()) {
            try {
                objectMapper.writeValue(nodeDetailsFile, nodeDetailsCollection);
            } catch (IOException e) {
                log.error("Failed to persist node details collection!", e);
            }
        }
    }

    public Collection<NodeInformation> restoreNodeDetails() {
        if (isPersistenceAvailable()) {
            try {
                List<NodeInformation> nodeDetails = objectMapper.readValue(
                        nodeDetailsFile, new TypeReference<List<NodeInformation>>() { });
                return nodeDetails;
            } catch (IOException e) {
                log.error("Failed to restore node details collection!", e);
            }
        }
        return Collections.emptyList();
    }

    private boolean isPersistenceAvailable() {
        return baseDir != null;
    }
}
