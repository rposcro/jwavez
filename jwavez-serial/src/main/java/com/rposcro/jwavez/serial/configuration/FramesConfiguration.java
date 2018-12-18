package com.rposcro.jwavez.serial.configuration;

import com.rposcro.jwavez.serial.frame.SOFFrameParser;
import com.rposcro.jwavez.serial.frame.SOFFrameRegistry;
import com.rposcro.jwavez.serial.frame.SOFFrameValidator;
import lombok.Getter;

@Getter
public class FramesConfiguration {

  private SOFFrameParser frameParser;
  private SOFFrameRegistry frameRegistry;
  private SOFFrameValidator frameValidator;

  public FramesConfiguration() {
    this.frameValidator = new SOFFrameValidator();
    this.frameRegistry = SOFFrameRegistry.defaultRegistry();
    this.frameParser = new SOFFrameParser(frameRegistry);
  }

}
