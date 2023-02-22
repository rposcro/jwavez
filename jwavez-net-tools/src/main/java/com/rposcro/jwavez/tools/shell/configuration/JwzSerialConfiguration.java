package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwzSerialConfiguration {

    @Bean
    public JwzSerialSupport jwzSerialSupport() {
        return new JwzSerialSupport();
    }

    @Bean
    public SerialRequestFactory serialRequestFactory() {
        return jwzSerialSupport().serialRequestFactory();
    }
}
