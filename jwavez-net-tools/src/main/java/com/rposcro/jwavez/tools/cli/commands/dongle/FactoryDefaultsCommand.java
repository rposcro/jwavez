package com.rposcro.jwavez.tools.cli.commands.dongle;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.commands.AbstractSyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.FactoryDefaultsOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FactoryDefaultsCommand extends AbstractSyncBasedCommand {

    private FactoryDefaultsOptions options;

    @Override
    public void configure(String[] args) throws CommandOptionsException {
        options = new FactoryDefaultsOptions(args);
    }

    @Override
    public void execute() {
        System.out.println("Resetting dongle to factory defaults " + options.getDevice() + "...");
        ProcedureUtil.executeProcedure(this::resetDongle);
        System.out.println("Dongle reset finished");
    }

    private void resetDongle() throws SerialException {
        connect(options);
        controller.requestCallbackFlow(
                JwzSerialSupport.defaultSupport().serialRequestFactory().deviceManagementRequestBuilder()
                        .createSetDefaultRequest(nextFlowId()),
                options.getTimeout());
        System.out.println("Factory defaults reset successful");
    }
}
