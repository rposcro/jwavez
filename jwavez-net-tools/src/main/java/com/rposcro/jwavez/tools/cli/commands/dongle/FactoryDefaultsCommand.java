package com.rposcro.jwavez.tools.cli.commands.dongle;

import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.SetDefaultRequest;
import com.rposcro.jwavez.tools.cli.commands.AbstractSyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.FactoryDefaultsOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FactoryDefaultsCommand extends AbstractSyncBasedCommand {

  private FactoryDefaultsOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new FactoryDefaultsOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    System.out.println("Resetting dongle to factory defaults " + options.getDevice() + "...");
    resetDongle();
    System.out.println("End of set factory defaults transaction");
  }

  private void resetDongle() {
    try {
      controller.requestCallbackFlow(
          SetDefaultRequest.createSetDefaultRequest(nextFlowId()),
          options.getTimeout());
      System.out.println("Factory defaults reset successful");
    } catch(SerialException e) {
      log.debug("Factory defaults failed due to an error", e);
      System.out.printf("Factory defaults failed due to an error: %s\n", e.getMessage());
    }
  }
}
