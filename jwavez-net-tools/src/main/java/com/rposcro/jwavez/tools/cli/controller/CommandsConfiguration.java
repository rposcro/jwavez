package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.commands.dongle.DongleCheckCommand;
import com.rposcro.jwavez.tools.cli.commands.network.ExcludeNodeCommand;
import com.rposcro.jwavez.tools.cli.commands.dongle.FactoryDefaultsCommand;
import com.rposcro.jwavez.tools.cli.commands.HelpCommand;
import com.rposcro.jwavez.tools.cli.commands.network.IncludeNodeCommand;
import com.rposcro.jwavez.tools.cli.commands.network.ListenerCommand;
import com.rposcro.jwavez.tools.cli.commands.network.NetworkLearnCommand;
import com.rposcro.jwavez.tools.cli.commands.node.NodeAssociationInfoCommand;
import com.rposcro.jwavez.tools.cli.commands.node.NodeAssociationRemoveCommand;
import com.rposcro.jwavez.tools.cli.commands.node.NodeAssociationSetCommand;
import com.rposcro.jwavez.tools.cli.commands.node.NodeConfigurationReadCommand;
import com.rposcro.jwavez.tools.cli.commands.node.NodeConfigurationSetCommand;
import com.rposcro.jwavez.tools.cli.commands.node.NodeInfoCommand;
import com.rposcro.jwavez.tools.cli.commands.network.SUCCommand;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceBasedOptions;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceTimeoutBasedOptions;
import com.rposcro.jwavez.tools.cli.options.node.DefaultNodeBasedOptions;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import com.rposcro.jwavez.tools.cli.options.FactoryDefaultsOptions;
import com.rposcro.jwavez.tools.cli.options.HelpOptions;
import com.rposcro.jwavez.tools.cli.options.NetworkLearnOptions;
import com.rposcro.jwavez.tools.cli.options.node.NodeAssociationOptions;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationReadOptions;
import com.rposcro.jwavez.tools.cli.options.SUCOptions;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationSetOptions;
import com.rposcro.jwavez.tools.cli.options.node.NodeInfoOptions;
import lombok.Getter;

@Getter
public class CommandsConfiguration {

  public static final String APP_CMD_NAME = "jwz";
  private static final CommandsConfiguration defaultConfiguration = new CommandsConfiguration();

  private CommandTree commandTree;
  private CommandHelpTool commandHelpTool;
  private CommandUsageTool commandUsageTool;

  private CommandsConfiguration() {
    this.commandTree = CommandTree.builder()
        .rootNode(new CommandTreeNode("", "")
          .addChild(helpCommand())
          .addChild(listenerCommand())
          .addChild(dongleInfoCommand())
          .addChild(sucCommand())
          .addChild(inclusionCommand())
          .addChild(exclusionCommand())
          .addChild(learnCommand())
          .addChild(purgeCommand())
          .addChild(nodeCommand())
        ).build();
    this.commandUsageTool = CommandUsageTool.builder()
        .commandTree(commandTree)
        .applicationCommandName(APP_CMD_NAME)
        .build();
    this.commandHelpTool = CommandHelpTool.builder()
        .commandTree(commandTree)
        .build();
  }

  public static CommandsConfiguration defaultConfiguration() {
    return defaultConfiguration;
  }

  private CommandTreeNode helpCommand() {
    return new CommandTreeNode("help", "displays help of given command")
        .withCommandReference(HelpCommand.class, HelpOptions.OPTIONS);
  }

  private CommandTreeNode dongleInfoCommand() {
    return new CommandTreeNode("info", "requests various controller and network information from dongle")
        .withCommandReference(DongleCheckCommand.class, DongleCheckOptions.OPTIONS);
  }

  private CommandTreeNode sucCommand() {
    return new CommandTreeNode("suc", "reads or sets SUC configuration on this dongle")
        .withCommandReference(SUCCommand.class, SUCOptions.OPTIONS);
  }

  private CommandTreeNode inclusionCommand() {
    return new CommandTreeNode("inclusion", "executes node inclusion process")
        .withCommandReference(IncludeNodeCommand.class, DefaultDeviceTimeoutBasedOptions.OPTIONS);
  }

  private CommandTreeNode exclusionCommand() {
    return new CommandTreeNode("exclusion", "executes node exclusion process")
        .withCommandReference(ExcludeNodeCommand.class, DefaultDeviceTimeoutBasedOptions.OPTIONS);
  }

  private CommandTreeNode learnCommand() {
    return new CommandTreeNode("learn", "enables learn mode on this dongle, enables inclusion into another network")
        .withCommandReference(NetworkLearnCommand.class, NetworkLearnOptions.OPTIONS);
  }

  private CommandTreeNode purgeCommand() {
    return new CommandTreeNode("purge", "resets dongle to factory defaults")
        .withCommandReference(FactoryDefaultsCommand.class, FactoryDefaultsOptions.OPTIONS);
  }

  private CommandTreeNode listenerCommand() {
    return new CommandTreeNode("listen", "listens to inbound frames")
        .withCommandReference(ListenerCommand.class, DefaultDeviceBasedOptions.OPTIONS);
  }

  private CommandTreeNode nodeCommand() {
    return new CommandTreeNode("node", "reads or sets configuration of nodes in network")
        .addChild(new CommandTreeNode("info", "reads node dongleDevice/command class information")
            .withCommandReference(NodeInfoCommand.class, NodeInfoOptions.OPTIONS))
        .addChild(new CommandTreeNode("association", "manages node group associations")
            .addChild(new CommandTreeNode("info", "reads associations information from node")
                .withCommandReference(NodeAssociationInfoCommand.class, DefaultNodeBasedOptions.OPTIONS))
            .addChild(new CommandTreeNode("set", "sets new node association to a group")
                .withCommandReference(NodeAssociationSetCommand.class, NodeAssociationOptions.OPTIONS))
            .addChild(new CommandTreeNode("remove", "removes node association from a group")
                .withCommandReference(NodeAssociationRemoveCommand.class, NodeAssociationOptions.OPTIONS)))
        .addChild(new CommandTreeNode("configuration", "manages node configuration parameters")
            .addChild(new CommandTreeNode("read", "reads node configuration parameters")
                .withCommandReference(NodeConfigurationReadCommand.class, NodeConfigurationReadOptions.OPTIONS))
            .addChild(new CommandTreeNode("set", "sets node configuration parameter")
                .withCommandReference(NodeConfigurationSetCommand.class, NodeConfigurationSetOptions.OPTIONS))
        );
  }
}
