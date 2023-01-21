package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWaveZConfiguration {

    @Bean
    public SupportedCommandParser supportedCommandParser() {
        return SupportedCommandParser.defaultParser();
    }
}
