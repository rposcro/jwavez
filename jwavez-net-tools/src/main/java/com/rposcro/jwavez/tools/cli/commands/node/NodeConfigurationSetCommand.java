package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationSetOptions;
import com.rposcro.jwavez.tools.utils.ProcedureUtil;

public class NodeConfigurationSetCommand extends AbstractNodeConfigurationCommand {

  private NodeConfigurationSetOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeConfigurationSetOptions(args);
  }

  @Override
  public void execute() {
    System.out.println("Setting configuration parameter value...");
    ProcedureUtil.executeProcedure(this::setConfiguration);
    checkConfiguration(options.getNodeId(), options.getParameterNumber(), options.getTimeout());
    System.out.println("Configuration set up finished");
  }

  private void setConfiguration() throws SerialException {
    connect(options);
    processSendDataRequest(
        options.getNodeId(),
        configurationCommandBuilder.buildSetParameterCommand(options.getParameterNumber(), options.getParameterValue(), options.getParameterSize()));
  }
}
