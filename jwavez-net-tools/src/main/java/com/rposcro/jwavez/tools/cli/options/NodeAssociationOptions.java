package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class NodeAssociationOptions extends AbstractNodeBasedOptions {

  private static final String OPT_ASSO_GROUP = "ag";
  private static final String OPT_ASSO_NODE = "an";

  public static final Options OPTIONS = CommandOptions.defaultNodeBasedOptions()
      .addOption(Option.builder(OPT_ASSO_GROUP)
          .longOpt("association-group")
          .required()
          .hasArg()
          .argName("groupId")
          .type(Number.class)
          .desc("group id where association is to be added").build())
      .addOption(Option.builder(OPT_ASSO_NODE)
          .longOpt("association-node")
          .required()
          .hasArg()
          .argName("nodeId")
          .type(Number.class)
          .desc("node id which is to be associated").build())
      ;

  private byte associationGroupId;
  private byte associationNodeId;

  public NodeAssociationOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
    try {
      associationGroupId = parseByte(OPT_ASSO_GROUP);
      associationNodeId = parseByte(OPT_ASSO_NODE);
    } catch(ParseException e) {
      throw new CommandOptionsException(e);
    }
  }
}
