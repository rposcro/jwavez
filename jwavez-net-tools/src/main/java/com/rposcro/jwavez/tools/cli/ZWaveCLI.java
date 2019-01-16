package com.rposcro.jwavez.tools.cli;

import com.rposcro.jwavez.tools.cli.controller.CommandController;

public class ZWaveCLI {

  private static final String APP_HEAD_LINE = "JWaveZ Network Tool 1.0";

  public static void main(String[] args) throws Exception {
    System.out.println(APP_HEAD_LINE);
    CommandController controller = new CommandController();
    controller.executeCommand(args);
//    controller.executeCommand("help", "-r", "info", "-f");
    System.exit(0);
  }
}
