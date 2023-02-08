package com.rposcro.jwavez.tools.shell.communication;

import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkController;
import com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class SerialControllerManager {

    @Autowired
    private JWaveZShellContext shellContext;

    private BasicSynchronousController basicSynchronousController;
    private GeneralAsynchronousController generalAsynchronousController;
    private ApplicationCommandExecutor applicationCommandExecutor;
    private SerialCallbackExecutor serialCallbackExecutor;
    private AddNodeToNetworkController addNodeToNetworkController;
    private RemoveNodeFromNetworkController removeNodeFromNetworkController;

    public BasicSynchronousController acquireBasicSynchronousController() throws SerialPortException {
        if (basicSynchronousController == null) {
            closeControllers();
            basicSynchronousController = BasicSynchronousController.builder()
                    .dongleDevice(shellContext.getDongleDevicePath())
                    .build()
                    .connect();
        }
        return basicSynchronousController;
    }

    public GeneralAsynchronousController acquireGeneralAsynchronousController(
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

    public ApplicationCommandExecutor acquireApplicationCommandExecutor(long timeoutMillis) throws SerialPortException {
        if (applicationCommandExecutor == null) {
            closeControllers();
            this.applicationCommandExecutor = ApplicationCommandExecutor.builder()
                    .device(shellContext.getDongleDevicePath())
                    .timeoutMillis(timeoutMillis)
                    .build();
        }
        return applicationCommandExecutor;
    }

    public SerialCallbackExecutor acquireSerialCallbackExecutor(long timeoutMillis) throws SerialPortException {
        if (serialCallbackExecutor == null) {
            closeControllers();
            this.serialCallbackExecutor = SerialCallbackExecutor.builder()
                    .device(shellContext.getDongleDevicePath())
                    .timeoutMillis(timeoutMillis)
                    .build();
        }
        return serialCallbackExecutor;
    }

    public AddNodeToNetworkController acquireAddNodeToNetworkController(long timeoutInMilliseconds) throws SerialPortException {
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

    public RemoveNodeFromNetworkController acquireRemoveNodeToNetworkController(long timeoutInMilliseconds) throws SerialPortException {
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

    public void closeControllers() throws SerialPortException {
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

        if (serialCallbackExecutor != null) {
            serialCallbackExecutor.close();
            serialCallbackExecutor = null;
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
        } catch (InterruptedException e) {
        }
    }
}
