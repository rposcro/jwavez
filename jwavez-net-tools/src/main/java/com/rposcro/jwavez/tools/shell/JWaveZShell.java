package com.rposcro.jwavez.tools.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

@SpringBootApplication(excludeName = { "org.springframework.shell.standard.StandardAPIAutoConfiguration" })
public class JWaveZShell {

    public static void main(String[] args) throws Exception {
//        String[] fullArgs = StringUtils.concatenateStringArrays(
//                args, new String[] { "--spring.shell.command.quit.enabled=false" });
        SpringApplication.run(JWaveZShell.class, args);
    }
}
