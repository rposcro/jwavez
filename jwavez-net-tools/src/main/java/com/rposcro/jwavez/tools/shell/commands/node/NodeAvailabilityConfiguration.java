package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;

@Configuration
public class NodeAvailabilityConfiguration {

    @Autowired
    private NodeScopeContext nodeContext;

    @Bean("nodeAvailability")
    public AvailabilityProvider nodeAvailabilityProvider() {
        return () -> nodeContext.isAnyNodeSelected() ?
            Availability.available() :
            Availability.unavailable("No node is selected in the working context, select node or use node-id option");
    }
}
