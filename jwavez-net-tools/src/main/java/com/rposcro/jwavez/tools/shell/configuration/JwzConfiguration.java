package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.core.JwzCommands;
import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.manufacturerspecific.ManufacturerSpecificCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.version.VersionCommandBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwzConfiguration {

    @Bean
    public SupportedCommandParser supportedCommandParser() {
        return SupportedCommandParser.defaultParser();
    }

    @Bean
    public JwzCommands jwzCommands() {
        return new JwzCommands();
    }

    @Bean
    public PowerLevelCommandBuilder powerLevelCommandBuilder() {
        return jwzCommands().controlledCommandFactory().powerLevelCommandBuilder();
    }

    @Bean
    public AssociationCommandBuilder associationCommandBuilder() {
        return jwzCommands().controlledCommandFactory().associationCommandBuilder();
    }

    @Bean
    public ManufacturerSpecificCommandBuilder manufacturerSpecificCommandBuilder() {
        return jwzCommands().controlledCommandFactory().manufacturerSpecificCommandBuilder();
    }

    @Bean
    public VersionCommandBuilder versionCommandBuilder() {
        return jwzCommands().controlledCommandFactory().versionCommandBuilder();
    }

    @Bean
    public ConfigurationCommandBuilder configurationCommandBuilder() {
        return jwzCommands().controlledCommandFactory().configurationCommandBuilder();
    }
}
