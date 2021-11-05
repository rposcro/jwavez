package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.tools.shell.ShellContext;
import com.rposcro.jwavez.tools.shell.scopes.TopScope;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.shell.jline.PromptProvider;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Configuration
public class ShellConfiguration {

    private String JWAVEZ_DEVICE_ENV = "JWAVEZ_DEVICE";

    @Bean
    public PromptProvider promptProvider() {
        return () -> new AttributedString("jwavez:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    public ShellContext shellContext(TopScope topScope) {
        String device = System.getenv(JWAVEZ_DEVICE_ENV);
        if (device == null) {
            System.out.println("Note! No device detected!");
        }

        ShellContext shellContext = ShellContext.builder()
                .device(device)
                .workingScope(topScope)
                .build();
        return shellContext;
    }
}
