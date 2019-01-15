package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.commands.DongleCheckCommand;
import com.rposcro.jwavez.tools.cli.commands.ExcludeNodeCommand;
import com.rposcro.jwavez.tools.cli.commands.FactoryDefaultsCommand;
import com.rposcro.jwavez.tools.cli.commands.HelpCommand;
import com.rposcro.jwavez.tools.cli.commands.IncludeNodeCommand;
import com.rposcro.jwavez.tools.cli.commands.NetworkLearnCommand;
import com.rposcro.jwavez.tools.cli.commands.NodeAssociationInfoCommand;
import com.rposcro.jwavez.tools.cli.commands.NodeAssociationRemoveCommand;
import com.rposcro.jwavez.tools.cli.commands.NodeAssociationSetCommand;
import com.rposcro.jwavez.tools.cli.commands.NodeInfoCommand;
import com.rposcro.jwavez.tools.cli.commands.SUCCommand;
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceBasedOptions;
import com.rposcro.jwavez.tools.cli.options.DefaultNodeBasedOptions;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import com.rposcro.jwavez.tools.cli.options.FactoryDefaultsOptions;
import com.rposcro.jwavez.tools.cli.options.HelpOptions;
import com.rposcro.jwavez.tools.cli.options.NetworkLearnOptions;
import com.rposcro.jwavez.tools.cli.options.NodeAssociationOptions;
import com.rposcro.jwavez.tools.cli.options.SUCOptions;
import lombok.Getter;

@Getter
public class CommandsConfiguration {

  public static final String APP_CMD_NAME = "jwzt";
  private static final CommandsConfiguration defaultConfiguration = new CommandsConfiguration();

  private CommandTree commandTree;
  private CommandHelpTool commandHelpTool;
  private CommandUsageTool commandUsageTool;

  private CommandsConfiguration() {
    this.commandTree = new CommandTree()
        .addNode(helpCommand())
        .addNode(dongleInfoCommand())
        .addNode(sucCommand())
        .addNode(inclusionCommand())
        .addNode(exclusionCommand())
        .addNode(learnCommand())
        .addNode(purgeCommand())
        .addNode(nodeCommand())
        ;
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
        .withCommandReference(IncludeNodeCommand.class, DefaultDeviceBasedOptions.OPTIONS);
  }

  private CommandTreeNode exclusionCommand() {
    return new CommandTreeNode("exclusion", "executes node exclusion process")
        .withCommandReference(ExcludeNodeCommand.class, DefaultDeviceBasedOptions.OPTIONS);
  }

  private CommandTreeNode learnCommand() {
    return new CommandTreeNode("learn", "enables learn mode on this dongle, enables inclusion into another network")
        .withCommandReference(NetworkLearnCommand.class, NetworkLearnOptions.OPTIONS);
  }

  private CommandTreeNode purgeCommand() {
    return new CommandTreeNode("purge", "resets dongle to factory defaults")
        .withCommandReference(FactoryDefaultsCommand.class, FactoryDefaultsOptions.OPTIONS);
  }

  private CommandTreeNode nodeCommand() {
    return new CommandTreeNode("node", "reads or sets configuration of nodes in network")
        .withChild(new CommandTreeNode("class", "reads node device/command class information")
            .withCommandReference(NodeInfoCommand.class, DefaultNodeBasedOptions.OPTIONS))
        .withChild(new CommandTreeNode("association", "manages node group associations")
            .withChild(new CommandTreeNode("info", "reads associations information from node")
              .withCommandReference(NodeAssociationInfoCommand.class, DefaultNodeBasedOptions.OPTIONS))
            .withChild(new CommandTreeNode("set", "sets new node association to a group")
              .withCommandReference(NodeAssociationSetCommand.class, NodeAssociationOptions.OPTIONS))
            .withChild(new CommandTreeNode("remove", "removes node association from a group")
              .withCommandReference(NodeAssociationRemoveCommand.class, NodeAssociationOptions.OPTIONS))
        );
  }
}
