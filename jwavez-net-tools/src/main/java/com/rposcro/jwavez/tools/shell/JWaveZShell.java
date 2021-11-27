package com.rposcro.jwavez.tools.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = { "org.springframework.shell.standard.StandardAPIAutoConfiguration" })
public class JWaveZShell {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(JWaveZShell.class, args);
    }
}
