package com.rposcro.jwavez.core.listeners;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SupportedCommandDispatcherTest {

    @Test
    public void dispatchesToDedicatedListener() {
        final ZWaveSupportedCommand basicSet = mockBasicSet();
        final SupportedCommandListener<BasicSet> commandListener = Mockito.mock(SupportedCommandListener.class);
        final SupportedCommandDispatcher dispatcher = new SupportedCommandDispatcher();
        final ArgumentCaptor<BasicSet> commandCaptor = ArgumentCaptor.forClass(BasicSet.class);

        dispatcher.registerHandler(BasicCommandType.BASIC_SET, commandListener);
        dispatcher.dispatchCommand(basicSet);

        verify(commandListener).handleCommand(commandCaptor.capture());
        BasicSet capturedCommand = commandCaptor.getValue();
        assertEquals(CommandClass.CMD_CLASS_BASIC, capturedCommand.getCommandClass());
        assertEquals(BasicCommandType.BASIC_SET, capturedCommand.getCommandType());
    }

    @Test
    public void doesntDispatchToDedicatedListener() {
        final ZWaveSupportedCommand configurationReport = mockConfigurationReport();
        final SupportedCommandListener<ZWaveSupportedCommand> commandListener = Mockito.mock(SupportedCommandListener.class);
        final SupportedCommandDispatcher dispatcher = new SupportedCommandDispatcher();

        dispatcher.registerHandler(BasicCommandType.BASIC_SET, commandListener);
        dispatcher.dispatchCommand(configurationReport);

        verify(commandListener, never()).handleCommand(Mockito.any(ZWaveSupportedCommand.class));
    }

    @Test
    public void dispatchesToGenericListener() {
        final ZWaveSupportedCommand configurationReport = mockConfigurationReport();
        final SupportedCommandListener<ZWaveSupportedCommand> commandListener = Mockito.mock(SupportedCommandListener.class);
        final SupportedCommandDispatcher dispatcher = new SupportedCommandDispatcher();
        final ArgumentCaptor<? extends ZWaveSupportedCommand> commandCaptor = ArgumentCaptor.forClass(ZWaveSupportedCommand.class);

        dispatcher.registerAllCommandsHandler(commandListener);
        dispatcher.dispatchCommand(configurationReport);

        verify(commandListener).handleCommand(commandCaptor.capture());
        ZWaveSupportedCommand capturedCommand = commandCaptor.getValue();
        assertEquals(CommandClass.CMD_CLASS_CONFIGURATION, capturedCommand.getCommandClass());
        assertEquals(ConfigurationCommandType.CONFIGURATION_REPORT, capturedCommand.getCommandType());
    }

    private BasicSet mockBasicSet() {
        return JwzSupportedCommandParser.defaultParser().parseCommand(
                ImmutableBuffer.overBuffer(new byte[] {0x20, 0x01, 0x00}),
                NodeId.forId(13));
    }

    private ConfigurationReport mockConfigurationReport() {
        return JwzSupportedCommandParser.defaultParser().parseCommand(
                ImmutableBuffer.overBuffer(new byte[] {0x70, 0x06, 0x01, 0x01, (byte) 0xff}),
                NodeId.forId(13));
    }
}
