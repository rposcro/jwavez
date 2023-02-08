package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.core.JwzApplicationCommands;
import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.manufacturerspecific.ManufacturerSpecificCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannelassociation.MultiChannelAssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
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
    public JwzApplicationCommands jwzApplicationCommands() {
        return new JwzApplicationCommands();
    }

    @Bean
    public AssociationCommandBuilder associationCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().associationCommandBuilder();
    }

    @Bean
    public ConfigurationCommandBuilder configurationCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().configurationCommandBuilder();
    }

    @Bean
    public ManufacturerSpecificCommandBuilder manufacturerSpecificCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().manufacturerSpecificCommandBuilder();
    }

    @Bean
    public MultiChannelAssociationCommandBuilder multiChannelAssociationCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().multiChannelAssociationCommandBuilder();
    }

    @Bean
    public MultiChannelCommandBuilder multiChannelCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().multiChannelCommandBuilder();
    }

    @Bean
    public PowerLevelCommandBuilder powerLevelCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().powerLevelCommandBuilder();
    }

    @Bean
    public SwitchBinaryCommandBuilder switchBinaryCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().switchBinaryCommandBuilder();
    }

    @Bean
    public SwitchColorCommandBuilder switchColorCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().switchColorCommandBuilder();
    }

    @Bean
    public SwitchMultiLevelCommandBuilder switchMultiLevelCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().switchMultiLevelCommandBuilder();
    }

    @Bean
    public VersionCommandBuilder versionCommandBuilder() {
        return jwzApplicationCommands().controlledCommandFactory().versionCommandBuilder();
    }
}
