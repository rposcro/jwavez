package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.communication.ApplicationCommandExecutor;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
public class SerialControllerManager {

    @Autowired
    private JWaveZShellContext shellContext;

    private BasicSynchronousController basicSynchronousController;
    private ApplicationCommandExecutor applicationCommandExecutor;
    private Semaphore shareLocked = new Semaphore(1);

    public <T> T runBasicSynchronousFunction(SerialFunction<BasicSynchronousController, T> function) throws SerialException {
        try {
            acquireLock();
            BasicSynchronousController controller = acquireBasicSynchronousController();
            return function.execute(controller);
        } finally {
            releaseLock();
        }
    }

    public <T> T runApplicationCommandFunction(SerialFunction<ApplicationCommandExecutor, T> function) throws SerialException {
        try {
            acquireLock();
            ApplicationCommandExecutor executor = acquireApplicationCommandExecutor();
            return function.execute(executor);
        } finally {
            releaseLock();
        }
    }

    private BasicSynchronousController acquireBasicSynchronousController() throws SerialPortException {
        if (basicSynchronousController == null) {
            closeControllers();
            basicSynchronousController = BasicSynchronousController.builder()
                    .dongleDevice(shellContext.getDevice())
                    .build()
                    .connect();
        }
        return basicSynchronousController;
    }

    private ApplicationCommandExecutor acquireApplicationCommandExecutor() throws SerialPortException {
        if (applicationCommandExecutor == null) {
            closeControllers();
            this.applicationCommandExecutor = new ApplicationCommandExecutor(shellContext.getDevice());
        }
        return applicationCommandExecutor;
    }

    private void releaseLock() {
        shareLocked.release();
    }

    private void acquireLock() {
        if (!shareLocked.tryAcquire()) {
            throw new IllegalStateException("Controller share is locked!");
        }
    }

    private void closeControllers() throws SerialPortException {
        if (basicSynchronousController != null) {
            basicSynchronousController.close();
            basicSynchronousController = null;
        }

        if (applicationCommandExecutor != null) {
            applicationCommandExecutor.close();
            applicationCommandExecutor = null;
        }

        try {
            Thread.sleep(200);  // to make sure OS releases hook before we attempt to connect another controller
        } catch(InterruptedException e) {
        }
    }
}
