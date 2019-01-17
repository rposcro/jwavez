package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class DongleCheckOptions implements CommandOptions {

  private static final String OPT_CCAPA = "cc";
  private static final String OPT_DCAPA = "cz";
  private static final String OPT_INIDT = "ci";
  private static final String OPT_NTIDS = "cd";
  private static final String OPT_GVERS = "cv";
  private static final String OPT_SUCID = "cs";
  private static final String OPT_PWRLV = "cp";
  private static final String OPT_LBRTP = "cl";

  public static final Options OPTIONS = CommandOptions.defaultDeviceBasedOptions()
      .addOption(new Option(OPT_CCAPA, "ctrl-capabilities", false, "controller capabilities"))
      .addOption(new Option(OPT_DCAPA, "capabilities", false, "zwave capabilities"))
      .addOption(new Option(OPT_INIDT, "init-data", false, "initial data"))
      .addOption(new Option(OPT_NTIDS, "network-ids", false, "network and dongle ids"))
      .addOption(new Option(OPT_GVERS, "version", false, "protocol version"))
      .addOption(new Option(OPT_SUCID, "suc-id", false, "SUC node id"))
      .addOption(new Option(OPT_PWRLV, "power-level", false, "power level"))
      .addOption(new Option(OPT_LBRTP, "library-type", false, "library type"))
      ;

  private boolean allChecks;
  private CommandLine commandLine;

  public DongleCheckOptions(String[] args) throws CommandOptionsException {
    try {
      CommandLineParser parser = new DefaultParser();
      commandLine = parser.parse(OPTIONS, args, false);
      allChecks = Stream.of(OPT_CCAPA, OPT_DCAPA, OPT_INIDT, OPT_NTIDS, OPT_GVERS, OPT_SUCID, OPT_PWRLV, OPT_LBRTP)
          .noneMatch(commandLine::hasOption);
    } catch(ParseException e) {
      throw new CommandOptionsException(e.getMessage(), e);
    }
  }

  public boolean runControllerCapabilities() {
    return allChecks || commandLine.hasOption(OPT_CCAPA);
  }

  public boolean runCapabilities() {
    return allChecks || commandLine.hasOption(OPT_DCAPA);
  }

  public boolean runInitialData() {
    return allChecks || commandLine.hasOption(OPT_INIDT);
  }

  public boolean runNetworkIds() {
    return allChecks || commandLine.hasOption(OPT_NTIDS);
  }

  public boolean runGetVersion() {
    return allChecks || commandLine.hasOption(OPT_GVERS);
  }

  public boolean runSucId() {
    return allChecks || commandLine.hasOption(OPT_SUCID);
  }

  public boolean runPowerLevel() {
    return allChecks || commandLine.hasOption(OPT_PWRLV);
  }

  public boolean runLibraryType() {
    return allChecks || commandLine.hasOption(OPT_LBRTP);
  }

  public String getDevice() {
    return commandLine.getOptionValue(OPT_DEVICE);
  }
}
