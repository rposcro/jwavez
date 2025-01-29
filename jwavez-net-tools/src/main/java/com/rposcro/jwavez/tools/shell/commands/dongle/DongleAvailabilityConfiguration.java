package com.rposcro.jwavez.tools.shell.commands.dongle;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;

@Configuration
public class DongleAvailabilityConfiguration {

    @Autowired
    private JWaveZShellContext shellContext;

    @Bean("dongleAvailability")
    public AvailabilityProvider dongleAvailabilityProvider() {
        return () -> shellContext.getDongleDevicePath() != null ?
            Availability.available() :
            Availability.unavailable("ZWave dongle device is not specified");
    }
}
