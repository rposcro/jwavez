package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationCommandResult<T extends ZWaveSupportedCommand> {

    private T acquiredSupportedCommand;
    private byte[] serialResponsePayload;
    private byte[] serialCallbackPayload;
    private ZWaveCallback serialCallback;
}
