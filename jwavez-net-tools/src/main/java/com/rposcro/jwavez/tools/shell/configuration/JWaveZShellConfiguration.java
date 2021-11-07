package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.ScopeContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.scopes.TopScopeContext;
import com.rposcro.jwavez.tools.shell.spring.ScopedPromptProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.shell.jline.PromptProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Configuration
public class JWaveZShellConfiguration {

    private String JWAVEZ_DEVICE_ENV = "JWAVEZ_DEVICE";

    @Bean
    public PromptProvider promptProvider(JWaveZShellContext shellContext) {
        return ScopedPromptProvider.builder()
                .shellContext(shellContext)
                .build();
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    public JWaveZShellContext shellContext(TopScopeContext topScope) {
        String device = System.getenv(JWAVEZ_DEVICE_ENV);
        if (device == null) {
            System.out.println("Note! No device detected!");
        }

        JWaveZShellContext shellContext = JWaveZShellContext.builder()
                .device(device)
                .scopeContext(topScope)
                .build();
        return shellContext;
    }

    @Bean
    public Map<ShellScope, ScopeContext> scopeContextMap(List<ScopeContext> scopeContexts) {
        return scopeContexts.stream().collect(Collectors.toMap(
                ScopeContext::getScope, Function.identity()
        ));
    }
}
