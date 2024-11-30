package com.rposcro.jwavez.tools.shell;

import org.springframework.boot.ResourceBanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan
public class JWaveZShell {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(JWaveZShell.class)
                .lazyInitialization(true)
                .banner(new ResourceBanner(new ClassPathResource("banner.txt")))
                .build()
                .run(args);
    }
}
