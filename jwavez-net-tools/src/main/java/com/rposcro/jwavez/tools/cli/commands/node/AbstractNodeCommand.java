package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.enums.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandDispatcher;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.commands.AbstractDeviceCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.options.AbstractNodeBasedOptions;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractNodeCommand extends AbstractDeviceCommand {

  protected static final long DEFAULT_TRANSACTION_TIMEOUT = 10_000;
  protected static final long DEFAULT_CALLBACK_TIMEOUT = 10_000;

  private Semaphore timeoutLock;
  private CompletableFuture currentFuture;
  private CommandType currentCommandType;

  protected AbstractNodeCommand() {
    this.timeoutLock = new Semaphore(1);
  }

  protected void connect(AbstractNodeBasedOptions options) {
    super.connect(options);
    SupportedCommandDispatcher commandDispatcher = new SupportedCommandDispatcher();
    ApplicationCommandDispatcher dispatcherInterceptor = ApplicationCommandDispatcher.builder()
        .supportedCommandParser(SupportedCommandParser.defaultParser())
        .supportedCommandDispatcher(commandDispatcher)
        .build();
    commandDispatcher.registerAllCommandsHandler(this::handleZWaveCommand);
    channelManager.addInboundFrameInterceptor(dispatcherInterceptor);
  }

  protected void executeTransaction(SerialTransaction transaction, long timeout)
      throws CommandExecutionException {
    try {
      timeoutLock.acquireUninterruptibly();
      processTransaction(transaction, timeout);
    } catch(CancellationException | InterruptedException | ExecutionException e) {
      throw new CommandExecutionException("Command execution failed due to: " + e.getMessage(), e);
    } finally {
      timeoutLock.release();
    }
  }

  protected ZWaveSupportedCommand requestZWaveCommand(SerialTransaction transaction, CommandType commandType, long timeout)
      throws CommandExecutionException {
    try {
      timeoutLock.acquireUninterruptibly();
      currentFuture = new CompletableFuture();
      currentCommandType = commandType;
      processTransaction(transaction, DEFAULT_TRANSACTION_TIMEOUT);
      return (ZWaveSupportedCommand) currentFuture.get(timeout, TimeUnit.MILLISECONDS);
    } catch(TimeoutException e) {
      throw new CommandExecutionException("Command execution failed due to timeout");
    } catch(CancellationException | InterruptedException | ExecutionException e) {
      throw new CommandExecutionException("Command execution failed due to: " + e.getMessage(), e);
    } finally {
      timeoutLock.release();
    }
  }

  private void processTransaction(SerialTransaction transaction, long transactionTimeout)
      throws CancellationException, InterruptedException, ExecutionException, CommandExecutionException {
    Future<TransactionResult<?>> futureResult = serialChannel.executeTransaction(transaction, transactionTimeout);
    TransactionResult<?> result = futureResult.get();
    if (result.getStatus() != TransactionStatus.Completed) {
      throw new CommandExecutionException("Command execution didn't complete successfully");
    }
  }

  private void handleZWaveCommand(ZWaveSupportedCommand command) {
    if (command.getCommandType() == currentCommandType) {
      currentFuture.complete(command);
    }
  }

}
