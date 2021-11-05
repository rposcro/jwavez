package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.tools.shell.ShellContext;
import com.rposcro.jwavez.tools.shell.spring.DynamicMethodTargetRegistrar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.standard.CommandValueProvider;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.FileValueProvider;
import org.springframework.shell.standard.StandardParameterResolver;
import org.springframework.shell.standard.ValueProvider;

/**
 * This configuration is to replace excluded "org.springframework.shell.standard.StandardAPIAutoConfiguration" configuration.
 * This is only because StandardMethodTargetRegistrar is not flexible enough, or rather all Shell concept is not allowing
 * to have list of commands dynamic, i.e. for this project it's expected to have commands available depending on scope.
 * Potentially to be removed when a better solution is provided by the Shell framework.
 */
@Configuration
public class StandardShellAPIConfiguration {

    @Bean
    public ValueProvider commandValueProvider(@Lazy CommandRegistry commandRegistry) {
        return new CommandValueProvider(commandRegistry);
    }

    @Bean
    public ValueProvider enumValueProvider() {
        return new EnumValueProvider();
    }

    @Bean
    public ValueProvider fileValueProvider() {
        return new FileValueProvider();
    }

    @Bean
    public MethodTargetRegistrar standardMethodTargetResolver(ShellContext shellContext, ApplicationContext applicationContext) {
        return DynamicMethodTargetRegistrar.builder()
                .applicationContext(applicationContext)
                .shellContext(shellContext)
                .build();
    }

    @Bean
    public ParameterResolver standardParameterResolver(@Qualifier("spring-shell") ConversionService conversionService) {
        return new StandardParameterResolver(conversionService);
    }
}
