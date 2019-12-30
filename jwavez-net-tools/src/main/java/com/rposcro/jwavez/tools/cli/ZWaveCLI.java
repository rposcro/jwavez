package com.rposcro.jwavez.tools.cli;

import com.rposcro.jwavez.tools.cli.controller.CommandController;
import com.rposcro.jwavez.tools.cli.options.CommandOptions;

import java.util.Arrays;
import java.util.stream.Stream;

public class ZWaveCLI {

  private static final String APP_HEAD_LINE = "JWaveZ Network Tool 1.1.0";


  private static String[] enhanceOptions(String... args) {
    final String deviceOption = "-" + CommandOptions.OPT_DEVICE;
    if (!Stream.of(args).anyMatch(deviceOption::equals) && System.getenv("JWAVEZ_DEVICE") != null) {
      String[] newArgs = Arrays.copyOf(args, args.length + 2);
      newArgs[args.length - 2] = deviceOption;
      newArgs[args.length - 1] = System.getenv("JWAVEZ_DEVICE");
      return newArgs;
    } else {
      return args;
    }
  }

  public static void main(String... args) throws Exception {
    System.out.println(APP_HEAD_LINE);
    CommandController controller = new CommandController();
    int exitCode = controller.executeCommand(enhanceOptions(args));
    System.exit(exitCode);
  }
}
