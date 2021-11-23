package com.rposcro.jwavez.tools.shell.configuration;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.ScopeContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.scopes.TopScopeContext;
import com.rposcro.jwavez.tools.shell.services.DongleInformationService;
import com.rposcro.jwavez.tools.shell.spring.ScopedPromptProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.shell.jline.PromptProvider;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Configuration
public class JWaveZShellConfiguration {

    private final static String WORKSPACE_PATH = ".config/jwavez/shell/repo";

    @Bean
    public PromptProvider promptProvider(JWaveZShellContext shellContext) {
        return ScopedPromptProvider.builder()
                .shellContext(shellContext)
                .build();
    }

    @Bean
    public Map<ShellScope, ScopeContext> scopeContextMap(List<ScopeContext> scopeContexts) {
        return scopeContexts.stream().collect(Collectors.toMap(
                ScopeContext::getScope, Function.identity()
        ));
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    public JWaveZShellContext shellContext(TopScopeContext topScope) throws SerialException {
        JWaveZShellContext shellContext = JWaveZShellContext.builder()
                .workspaceDir(ensureWorkspaceDir())
                .scopeContext(topScope)
                .build();
        return shellContext;
    }

    private File ensureWorkspaceDir() {
        File homeDir = new File(System.getProperty("user.home"));
        File workspaceDir = new File(homeDir, WORKSPACE_PATH);
        if (!workspaceDir.exists()) {
            workspaceDir.mkdirs();
        } else if (workspaceDir.exists() && !workspaceDir.isDirectory()) {
            throw new IllegalStateException("Workspace directory is not usable! " + workspaceDir.getAbsolutePath());
        }
        return workspaceDir;
    }
}
