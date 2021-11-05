package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.shell.ShellContext;
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
    private ShellContext shellContext;

    private BasicSynchronousController basicSynchronousController;
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
    }
}
