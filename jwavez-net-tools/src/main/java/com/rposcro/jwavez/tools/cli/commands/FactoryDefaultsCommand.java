package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialChannelManager;
import com.rposcro.jwavez.serial.frame.callbacks.SetDefaultCallbackFrame;
import com.rposcro.jwavez.serial.frame.requests.SetDefaultRequestFrame;
import com.rposcro.jwavez.serial.transactions.RequestCallbackFlowTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.FactoryDefaultsOptions;
import java.util.concurrent.Future;

public class FactoryDefaultsCommand implements Command {

  private FactoryDefaultsOptions options;
  private SerialChannel serialChannel;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new FactoryDefaultsOptions(args);
  }

  @Override
  public void execute() {
    serialChannel = SerialChannelManager.builder()
        .device(options.getDevice())
        .manageThreads(true)
        .build()
        .connect();
    System.out.println("Resetting dongle to factory defaults " + options.getDevice() + "...");
    Future<TransactionResult<SetDefaultCallbackFrame>> futureResult = launchTransaction();
    System.out.println("Awaiting callback ...");
    processResult(futureResult);
    System.out.println("End of set factory defaults transaction");
  }

  private Future<TransactionResult<SetDefaultCallbackFrame>> launchTransaction() {
    RequestCallbackFlowTransaction<SetDefaultCallbackFrame> transaction = new RequestCallbackFlowTransaction<>(
        callbackId -> new SetDefaultRequestFrame(callbackId),
        SetDefaultCallbackFrame.class);
    return serialChannel.executeTransaction(transaction, options.getTimeout());
  }

  private void processResult(Future<TransactionResult<SetDefaultCallbackFrame>> futureResult) {
    try {
      TransactionResult<SetDefaultCallbackFrame> result = futureResult.get();
      if (result.getStatus() == TransactionStatus.Completed) {
        System.out.println("Factory defaults reset successful");
      } else if (result.getStatus() == TransactionStatus.Cancelled) {
        System.out.println("Factory defaults reset interrupted due to timeout");
      } else {
        System.out.println("Factory defaults reset failed");
      }
    } catch(Exception e) {
      System.out.println("Factory defaults reset interrupted by an error: " + e.getMessage());
    }
  }
}
