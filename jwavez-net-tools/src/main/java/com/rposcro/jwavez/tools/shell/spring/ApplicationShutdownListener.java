package com.rposcro.jwavez.tools.shell.spring;

import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationShutdownListener implements ApplicationListener<ContextStoppedEvent> {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
        try {
            log.info("Closing serial communication hooks ...");
            serialCommunicationService.releaseAllHooks();
            log.info("Closed");
        } catch(SerialPortException e) {
            log.error("Failed to release hooks!", e);
        }
    }
}