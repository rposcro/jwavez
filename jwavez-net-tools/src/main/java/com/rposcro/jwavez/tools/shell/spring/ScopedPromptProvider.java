package com.rposcro.jwavez.tools.shell.spring;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import lombok.Builder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;

@Builder
public class ScopedPromptProvider implements PromptProvider {

    private JWaveZShellContext shellContext;

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("jwavez" + shellContext.getShellScope().getScopePath(":") + ">",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}
