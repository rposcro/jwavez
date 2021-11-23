package com.rposcro.jwavez.tools.shell;

import com.rposcro.jwavez.tools.shell.spring.ContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(excludeName = { "org.springframework.shell.standard.StandardAPIAutoConfiguration" })
public class JWaveZShell {

    public static void main(String[] args) throws Exception {
//        String[] fullArgs = StringUtils.concatenateStringArrays(
//                args, new String[] { "--spring.shell.command.quit.enabled=false" });
//        new SpringApplicationBuilder(JWaveZShell.class)
//                .listeners(new ContextListener())
//                .build(args)
//                .run(JWaveZShell.class);
        SpringApplication.run(JWaveZShell.class, args);
    }
}
