package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.spring.DynamicCommandCatalog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandCatalogCustomizer;
import org.springframework.shell.command.CommandResolver;
import org.springframework.shell.context.ShellContext;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SpringShellApiConfiguration {

    @Bean
    public CommandCatalog commandCatalog(
        ObjectProvider<MethodTargetRegistrar> methodTargetRegistrars,
        ObjectProvider<CommandResolver> commandResolvers,
        ObjectProvider<CommandCatalogCustomizer> commandCatalogCustomizers,
        ShellContext shellContext,
        JWaveZShellContext jWaveZShellContext)
    {
        List<CommandResolver> resolvers = commandResolvers.orderedStream().collect(Collectors.toList());
        DynamicCommandCatalog commandCatalog = DynamicCommandCatalog.builder()
            .shellContext(shellContext)
            .jWaveZShellContext(jWaveZShellContext)
            .resolvers(resolvers)
            .build();

        methodTargetRegistrars.orderedStream().forEach((resolver) -> {
            resolver.register(commandCatalog);
        });
        commandCatalogCustomizers.orderedStream().forEach((customizer) -> {
            customizer.customize(commandCatalog);
        });

        return commandCatalog;
    }
}
