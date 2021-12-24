package com.rposcro.jwavez.tools.shell;

import org.springframework.boot.ResourceBanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication(excludeName = { "org.springframework.shell.standard.StandardAPIAutoConfiguration" })
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
