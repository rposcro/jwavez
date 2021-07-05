package com.rposcro.jwavez.tools.cli.options.node;

import com.rposcro.jwavez.core.constants.BitLength;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.CommandOptions;
import lombok.Getter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@Getter
public class NodeConfigurationSetOptions extends AbstractNodeBasedOptions {

  private static final String OPT_PARAM_NUMBER = "pn";
  private static final String OPT_PARAM_VALUE = "pv";
  private static final String OPT_PARAM_SIZE = "ps";

  public static final Options OPTIONS = CommandOptions.defaultNodeBasedOptions()
      .addOption(Option.builder(OPT_PARAM_NUMBER)
          .longOpt("parameter-number")
          .required()
          .hasArg()
          .argName("param")
          .type(Number.class)
          .desc("parameter id where value is to be set").build())
      .addOption(Option.builder(OPT_PARAM_VALUE)
          .longOpt("parameter-value")
          .required()
          .hasArg()
          .argName("number")
          .type(Number.class)
          .desc("value to be set").build())
      .addOption(Option.builder(OPT_PARAM_SIZE)
          .longOpt("parameter-size")
          .required()
          .hasArg()
          .argName("number")
          .type(Number.class)
          .desc("value size in bytes, only 1,2 or 4 are allowed").build())
      ;

  private int parameterNumber;
  private int parameterValue;
  private BitLength parameterSize;

  public NodeConfigurationSetOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
    try {
      parameterNumber = parseInteger(OPT_PARAM_NUMBER);
      parameterValue = parseInteger(OPT_PARAM_VALUE);
      parameterSize = BitLength.ofBytesNumber(parseInteger(OPT_PARAM_SIZE));
    } catch(ParseException e) {
      throw new CommandOptionsException("Wrong option format: " + e.getMessage(), e);
    } catch(IllegalArgumentException e) {
      throw new CommandOptionsException("Wrong size option: " + e.getMessage(), e);
    }
  }
}
