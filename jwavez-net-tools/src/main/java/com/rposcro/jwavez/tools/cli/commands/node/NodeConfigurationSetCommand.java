package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationSetOptions;

public class NodeConfigurationSetCommand extends AbstractNodeConfigurationCommand {

  private NodeConfigurationSetOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeConfigurationSetOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    setConfiguration();
    checkConfiguration(options.getNodeId(), options.getParameterNumber(), options.getTimeout());
  }

  private void setConfiguration() throws CommandExecutionException {
    System.out.println("Setting configuration parameter value...");
    try {
      processSendDataRequest(
          options.getNodeId(),
          configurationCommandBuilder.buildSetParameterCommand(options.getParameterNumber(), options.getParameterValue(), options.getParameterSize()));
      System.out.println("Configuration set successful");
    } catch(SerialException e) {
      System.out.println("Configuration set failed: " + e.getMessage());
    }
    System.out.println();
  }
}
