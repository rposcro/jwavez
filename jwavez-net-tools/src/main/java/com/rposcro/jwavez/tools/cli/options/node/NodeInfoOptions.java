package com.rposcro.jwavez.tools.cli.options.node;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.CommandOptions;
import lombok.Getter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

@Getter
public class NodeInfoOptions extends AbstractNodeBasedOptions {

  private static final String OPT_CLASS_VERSIONS = "vc";
  private static final String OPT_PROTOCOL_VERSIONS = "vp";
  private static final String OPT_MANUFACTURERE_SPECIFIC_INFO = "ms";

  public static final Options OPTIONS = CommandOptions.defaultNodeBasedOptions()
      .addOption(Option.builder(OPT_CLASS_VERSIONS)
          .longOpt("class-versions")
          .optionalArg(true)
          .desc("fetches commands versions").build())
      .addOption(Option.builder(OPT_PROTOCOL_VERSIONS)
          .longOpt("protocol-versions")
          .optionalArg(true)
          .desc("fetches protocol implementation versions").build())
      .addOption(Option.builder(OPT_MANUFACTURERE_SPECIFIC_INFO)
          .longOpt("manufacturer-specific")
          .optionalArg(true)
          .desc("fetches manufacturer specific information").build())
          ;

  boolean checkCommandsVersions;
  boolean checkProtocolVersions;
  boolean manufacturerSpecificInfo;

  public NodeInfoOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
    checkCommandsVersions = commandLine.hasOption(OPT_CLASS_VERSIONS);
    checkProtocolVersions = commandLine.hasOption(OPT_PROTOCOL_VERSIONS);
    manufacturerSpecificInfo = commandLine.hasOption(OPT_MANUFACTURERE_SPECIFIC_INFO);
  }
}
