package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
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
public class JwzApplicationConfiguration {

    @Bean
    public JwzApplicationSupport jwzApplicationSupport() {
        return new JwzApplicationSupport();
    }

    @Bean
    public JwzSupportedCommandParser supportedCommandParser() {
        return jwzApplicationSupport().supportedCommandParser();
    }

    @Bean
    public AssociationCommandBuilder associationCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().associationCommandBuilder();
    }

    @Bean
    public ConfigurationCommandBuilder configurationCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().configurationCommandBuilder();
    }

    @Bean
    public ManufacturerSpecificCommandBuilder manufacturerSpecificCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().manufacturerSpecificCommandBuilder();
    }

    @Bean
    public MultiChannelAssociationCommandBuilder multiChannelAssociationCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().multiChannelAssociationCommandBuilder();
    }

    @Bean
    public MultiChannelCommandBuilder multiChannelCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().multiChannelCommandBuilder();
    }

    @Bean
    public PowerLevelCommandBuilder powerLevelCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().powerLevelCommandBuilder();
    }

    @Bean
    public SwitchBinaryCommandBuilder switchBinaryCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().switchBinaryCommandBuilder();
    }

    @Bean
    public SwitchColorCommandBuilder switchColorCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().switchColorCommandBuilder();
    }

    @Bean
    public SwitchMultiLevelCommandBuilder switchMultiLevelCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().switchMultiLevelCommandBuilder();
    }

    @Bean
    public VersionCommandBuilder versionCommandBuilder() {
        return jwzApplicationSupport().controlledCommandFactory().versionCommandBuilder();
    }
}
