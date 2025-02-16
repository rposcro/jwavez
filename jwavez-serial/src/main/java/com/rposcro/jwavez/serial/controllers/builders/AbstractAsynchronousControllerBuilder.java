package com.rposcro.jwavez.serial.controllers.builders;

import com.rposcro.jwavez.core.exceptions.AssertionException;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Getter
public class AbstractAsynchronousControllerBuilder<T extends AbstractAsynchronousControllerBuilder> extends AbstractClosableControllerBuilder<T> {

    private RxTxRouterProcess rxTxRouterProcess;
    private CallbackFlowIdDispatcher callbackFlowIdDispatcher;
    private ExecutorService executorService;
    private boolean selfExecutor;

    public T rxTxRouterProcess(RxTxRouterProcess rxTxRouterProcess) {
        if (this.getRxTxConfiguration() != null) {
            throw new IllegalStateException("Either RxTxRouterProcess or RxTxRouterConfiguration can be used to build the controller!");
        }
        this.rxTxRouterProcess = rxTxRouterProcess;
        super.rxTxConfiguration(rxTxRouterProcess.getConfiguration());
        return (T) this;
    }

    @Override
    public T rxTxConfiguration(RxTxConfiguration rxTxConfiguration) {
        if (this.rxTxRouterProcess != null) {
            throw new IllegalStateException("Either RxTxRouterProcess or RxTxRouterConfiguration can be used to build the controller!");
        }
        return super.rxTxConfiguration(rxTxConfiguration);
    }

    public T callbackFlowIdDispatcher(CallbackFlowIdDispatcher callbackFlowIdDispatcher) {
        this.callbackFlowIdDispatcher = callbackFlowIdDispatcher;
        return (T) this;
    }

    public T executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return (T) this;
    }

    protected void ensureBuildReadiness(ResponseHandler responseHandler, CallbackHandler callbackHandler) {
        if (rxTxRouterProcess == null && getRxTxConfiguration() == null) {
            throw new AssertionException("Either RxTxRouterProcess or RxTxRouterConfiguration needs to be set!");
        } else if (rxTxRouterProcess == null) {
            this.rxTxRouterProcess = RxTxRouterProcess.builder()
                .configuration(getRxTxConfiguration())
                .serialPort(getSerialPort())
                .responseHandler(responseHandler)
                .callbackHandler(callbackHandler)
                .build();
        }

        if (callbackFlowIdDispatcher == null) {
            callbackFlowIdDispatcher = CallbackFlowIdDispatcher.shared();
        }

        if (executorService == null) {
            Executors.newSingleThreadExecutor(this::makeThread);
            this.selfExecutor = true;
        }

        super.ensureBuildReadiness();
    }

    private Thread makeThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(GeneralAsynchronousController.class.getSimpleName() + ".RxTxRouterThread");
        thread.setDaemon(true);
        return thread;
    }
}
