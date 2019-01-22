package com.rposcro.jwavez.tools.cli;

import com.rposcro.jwavez.tools.cli.controller.CommandController;

public class ZWaveCLI {

  private static final String APP_HEAD_LINE = "JWaveZ Network Tool 1.0.3";

  public static void main(String[] args) throws Exception {
    System.out.println(APP_HEAD_LINE);
    CommandController controller = new CommandController();
    int exitCode = controller.executeCommand(args);
    System.exit(exitCode);
  }
}
