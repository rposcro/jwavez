package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;

@Configuration
public class AvailabilityConfiguration {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeContext;

    @Bean("dongleAvailability")
    public AvailabilityProvider dongleAvailabilityProvider() {
        return () -> shellContext.getDongleDevicePath() != null ?
            Availability.available() :
            Availability.unavailable("ZWave dongle device is not specified");
    }

    @Bean("repositoryAvailability")
    public AvailabilityProvider repositoryAvailabilityProvider() {
        return () -> shellContext.isRepositoryOpened() ?
            Availability.available() :
            Availability.unavailable("No repository is opened");
    }

    @Bean("nodeAvailability")
    public AvailabilityProvider nodeAvailabilityProvider() {
        return () -> nodeContext.isAnyNodeSelected() ?
            Availability.available() :
            Availability.unavailable("No node is selected in the working context, select node or use node-id option");
    }
}
