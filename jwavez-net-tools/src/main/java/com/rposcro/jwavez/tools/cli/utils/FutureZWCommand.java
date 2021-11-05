package com.rposcro.jwavez.tools.cli.utils;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Builder
public class FutureZWCommand {

  @Getter
  private ZWaveCallback solicitedCallback;
  private Future<ZWaveSupportedCommand> supportedCommand;

  public ZWaveSupportedCommand getSupportedCommand() throws InterruptedException, ExecutionException {
    return supportedCommand.get();
  }
}
