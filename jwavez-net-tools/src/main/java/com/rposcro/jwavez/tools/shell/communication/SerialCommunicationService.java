package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkController;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import com.rposcro.jwavez.tools.utils.SerialProducer;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class SerialCommunicationService {

    @Autowired
    private SerialControllerManager serialControllerManager;

    private Semaphore shareLock = new Semaphore(1);

    public <T> T runBasicSynchronousFunction(SerialFunction<BasicSynchronousController, T> function) throws SerialException {
        return runSynchronously(() -> {
            BasicSynchronousController controller = serialControllerManager.acquireBasicSynchronousController();
            return function.execute(controller);
        });
    }

    public <T> T runGeneralAsynchronousFunction(
            SerialFunction<GeneralAsynchronousController, T> function,
            CallbackHandler callbackHandler
    ) throws SerialException {
        return runSynchronously(() -> {
            GeneralAsynchronousController controller = serialControllerManager.acquireGeneralAsynchronousController(callbackHandler);
            return function.execute(controller);
        });
    }

    public <T> T runSerialCallbackFunction(SerialFunction<SerialCallbackExecutor, T> function) throws SerialException {
        return runSerialCallbackFunction(function, SerialUtils.DEFAULT_TIMEOUT);
    }

    public <T> T runSerialCallbackFunction(SerialFunction<SerialCallbackExecutor, T> function, long timeoutMillis) throws SerialException {
        return runSynchronously(() -> {
            SerialCallbackExecutor executor = serialControllerManager.acquireSerialCallbackExecutor(timeoutMillis);
            return function.execute(executor);
        });
    }

    public <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> runApplicationCommandFunction(
            SerialFunction<ApplicationCommandExecutor, ApplicationCommandResult<T>> function) throws SerialException {
        return runApplicationCommandFunction(function, SerialUtils.DEFAULT_TIMEOUT);
    }

    public <T extends ZWaveSupportedCommand> ApplicationCommandResult<T> runApplicationCommandFunction(
            SerialFunction<ApplicationCommandExecutor, ApplicationCommandResult<T>> function, long timeoutMillis
    ) throws SerialException {
        return runSynchronously(() -> {
            ApplicationCommandExecutor executor = serialControllerManager.acquireApplicationCommandExecutor(timeoutMillis);
            return function.execute(executor);
        });
    }

    public NodeInfo runNodeInclusion(long timeOutMillis) throws SerialException {
        return runSynchronously(() -> {
            AddNodeToNetworkController controller = serialControllerManager.acquireAddNodeToNetworkController(timeOutMillis);
            NodeInfo nodeInfo = controller.listenForNodeToAdd().orElse(null);
            return nodeInfo;
        });
    }

    public NodeInfo runNodeExclusion(long timeOutMillis) throws SerialException {
        return runSynchronously(() -> {
            RemoveNodeFromNetworkController controller = serialControllerManager.acquireRemoveNodeToNetworkController(timeOutMillis);
            NodeInfo nodeInfo = controller.listenForNodeToRemove().orElse(null);
            return nodeInfo;
        });
    }

    public void releaseAllHooks() throws SerialPortException {
        serialControllerManager.closeControllers();
    }

    private <T> T runSynchronously(SerialProducer<T> producer) throws SerialException {
        try {
            acquireLock();
            return producer.execute();
        } finally {
            serialControllerManager.closeControllers();
            releaseLock();
        }
    }

    private void releaseLock() {
        shareLock.release();
    }

    private void acquireLock() {
        if (!shareLock.tryAcquire()) {
            throw new IllegalStateException("Controller share is locked!");
        }
    }
}
