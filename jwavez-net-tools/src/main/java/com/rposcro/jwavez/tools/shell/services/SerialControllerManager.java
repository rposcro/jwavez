package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkController;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.communication.ApplicationCommandExecutor;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class SerialControllerManager {

    @Autowired
    private JWaveZShellContext shellContext;

    private BasicSynchronousController basicSynchronousController;
    private GeneralAsynchronousController generalAsynchronousController;
    private ApplicationCommandExecutor applicationCommandExecutor;
    private AddNodeToNetworkController addNodeToNetworkController;
    private RemoveNodeFromNetworkController removeNodeFromNetworkController;
    private Semaphore shareLock = new Semaphore(1);

    public <T> T runBasicSynchronousFunction(SerialFunction<BasicSynchronousController, T> function) throws SerialException {
        try {
            acquireLock();
            BasicSynchronousController controller = acquireBasicSynchronousController();
            return function.execute(controller);
        } catch(SerialException e) {
            throw e;
        } finally {
            closeControllers();
            releaseLock();
        }
    }

    public <T> T runGeneralAsynchronousFunction(
            SerialFunction<GeneralAsynchronousController, T> function,
            CallbackHandler callbackHandler
    ) throws SerialException {
        try {
            acquireLock();
            GeneralAsynchronousController controller = acquireGeneralAsynchronousController(callbackHandler);
            return function.execute(controller);
        } finally {
            closeControllers();
            releaseLock();
        }
    }

    public <T> T runApplicationCommandFunction(SerialFunction<ApplicationCommandExecutor, T> function) throws SerialException {
        return runApplicationCommandFunction(function, SerialUtils.DEFAULT_TIMEOUT);
    }

    public <T> T runApplicationCommandFunction(SerialFunction<ApplicationCommandExecutor, T> function, long timeoutMillis
    ) throws SerialException {
        try {
            acquireLock();
            ApplicationCommandExecutor executor = acquireApplicationCommandExecutor(timeoutMillis);
            return function.execute(executor);
        } catch(SerialException e) {
            throw e;
        } finally {
            closeControllers();
            releaseLock();
        }
    }

    public NodeInfo runNodeInclusion(long timeoutInMilliseconds) throws SerialException {
        try {
            acquireLock();
            AddNodeToNetworkController controller = acquireAddNodeToNetworkController(timeoutInMilliseconds);
            NodeInfo nodeInfo = controller.listenForNodeToAdd().orElse(null);
            return nodeInfo;
        } finally {
            closeControllers();
            releaseLock();
        }
    }

    public NodeInfo runNodeExclusion(long timeoutInMilliseconds) throws SerialException {
        try {
            acquireLock();
            RemoveNodeFromNetworkController controller = acquireRemoveNodeToNetworkController(timeoutInMilliseconds);
            NodeInfo nodeInfo = controller.listenForNodeToRemove().orElse(null);
            return nodeInfo;
        } finally {
            closeControllers();
            releaseLock();
        }
    }

    public void releaseAllHooks() throws SerialPortException {
        closeControllers();
    }

    private BasicSynchronousController acquireBasicSynchronousController() throws SerialPortException {
        if (basicSynchronousController == null) {
            closeControllers();
            basicSynchronousController = BasicSynchronousController.builder()
                    .dongleDevice(shellContext.getDongleDevicePath())
                    .build()
                    .connect();
        }
        return basicSynchronousController;
    }

    private GeneralAsynchronousController acquireGeneralAsynchronousController(
            CallbackHandler callbackHandler
    ) throws SerialException {
        if (generalAsynchronousController == null) {
            closeControllers();
            generalAsynchronousController = GeneralAsynchronousController.builder()
                    .dongleDevice(shellContext.getDongleDevicePath())
                    .callbackHandler(callbackHandler)
                    .build()
                    .connect();
        }
        return generalAsynchronousController;
    }

    private ApplicationCommandExecutor acquireApplicationCommandExecutor(long timeoutMillis) throws SerialPortException {
        if (applicationCommandExecutor == null) {
            closeControllers();
            this.applicationCommandExecutor = ApplicationCommandExecutor.builder()
                    .device(shellContext.getDongleDevicePath())
                    .timeoutMillis(timeoutMillis)
                    .build();
        }
        return applicationCommandExecutor;
    }

    private AddNodeToNetworkController acquireAddNodeToNetworkController(long timeoutInMilliseconds) throws SerialPortException {
        if (addNodeToNetworkController == null) {
            closeControllers();
            this.addNodeToNetworkController = AddNodeToNetworkController.builder()
                    .dongleDevice(shellContext.getDongleDevicePath())
                    .waitForTouchTimeout(timeoutInMilliseconds)
                    .build()
                    .connect();
        }
        return addNodeToNetworkController;
    }

    private RemoveNodeFromNetworkController acquireRemoveNodeToNetworkController(long timeoutInMilliseconds) throws SerialPortException {
        if (removeNodeFromNetworkController == null) {
            closeControllers();
            this.removeNodeFromNetworkController = RemoveNodeFromNetworkController.builder()
                    .dongleDevice(shellContext.getDongleDevicePath())
                    .waitForTouchTimeout(timeoutInMilliseconds)
                    .build()
                    .connect();
        }
        return removeNodeFromNetworkController;
    }

    private void releaseLock() {
        shareLock.release();
    }

    private void acquireLock() {
        if (!shareLock.tryAcquire()) {
            throw new IllegalStateException("Controller share is locked!");
        }
    }

    private void closeControllers() throws SerialPortException {
        if (basicSynchronousController != null) {
            basicSynchronousController.close();
            basicSynchronousController = null;
        }

        if (generalAsynchronousController != null) {
            generalAsynchronousController.close();
            generalAsynchronousController = null;
        }

        if (applicationCommandExecutor != null) {
            applicationCommandExecutor.close();
            applicationCommandExecutor = null;
        }

        if (addNodeToNetworkController != null) {
            addNodeToNetworkController.close();
            addNodeToNetworkController = null;
        }

        if (removeNodeFromNetworkController != null) {
            removeNodeFromNetworkController.close();
            removeNodeFromNetworkController = null;
        }

        try {
            Thread.sleep(200);  // to make sure OS releases hook before we attempt to connect another controller
        } catch(InterruptedException e) {
        }
    }
}
