package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SUCOptions extends AbstractDeviceBasedOptions {

  private static final String OPT_DEVICE = "d";
  private static final String OPT_READ = "r";
  private static final String OPT_SET_OTHER = "so";
  private static final String OPT_SET_THIS = "st";

  public static final Options OPTIONS;

  static {
    OPTIONS = new Options().addOption(Option.builder(OPT_DEVICE).longOpt("device").required().hasArg().argName("device").desc("controller dongle device").build());
    OptionGroup group = new OptionGroup()
            .addOption(Option.builder(OPT_READ).longOpt("read").desc("reads suc id from this dongle").build())
            .addOption(Option.builder(OPT_SET_THIS).longOpt("set-this").desc("sets this dongle as suc").build())
            .addOption(Option.builder(OPT_SET_OTHER).longOpt("set-other").hasArg().type(Number.class).desc("sets suc id of other node").argName("node").build())
    ;
    group.setRequired(true);
    OPTIONS.addOptionGroup(group);
  }

  private Action action;
  private byte otherId;

  public SUCOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args, OPT_DEVICE);
    try {
      this.action = commandLine.hasOption(OPT_READ) ? Action.READ :
          commandLine.hasOption(OPT_SET_THIS) ? Action.SET_THIS : Action.SET_OTHER;
      if (action == Action.SET_OTHER) {
        this.otherId = ((Number) commandLine.getParsedOptionValue(OPT_SET_OTHER)).byteValue();
      }
    } catch(ParseException e) {
      throw new CommandOptionsException(e);
    }
  }

  public Action getAction() {
    return this.action;
  }

  public byte getOtherId() {
    return this.otherId;
  }

  public static enum Action {
    READ,
    SET_THIS,
    SET_OTHER
  }
}
