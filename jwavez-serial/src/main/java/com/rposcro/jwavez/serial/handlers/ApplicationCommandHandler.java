package com.rposcro.jwavez.serial.handlers;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.model.FrameCast;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationCommandHandler implements CallbackHandler {

    private final static byte APP_COMMAND_CODE = SerialCommand.APPLICATION_COMMAND_HANDLER.getCode();

    private InboundFrameParser frameParser;
    private JwzSupportedCommandParser supportedCommandParser;
    private SupportedCommandDispatcher supportedCommandDispatcher;

    private boolean supportMulticasts;
    private boolean supportBroadcasts;

    @Builder
    public ApplicationCommandHandler(
            SupportedCommandDispatcher supportedCommandDispatcher,
            boolean supportMulticasts,
            boolean supportBroadcasts) {
        this.supportBroadcasts = supportBroadcasts;
        this.supportMulticasts = supportMulticasts;
        this.supportedCommandDispatcher = supportedCommandDispatcher;
        this.supportedCommandParser = new JwzSupportedCommandParser(SupportedCommandResolversRegistry.instance());
        this.frameParser = InboundFrameParser.defaultParser();
    }

    @Override
    public void accept(ViewBuffer buffer) {
        if (buffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND) == APP_COMMAND_CODE) {
            try {
                ApplicationCommandHandlerCallback callback = (ApplicationCommandHandlerCallback) frameParser.parseCallbackFrame(buffer);
                if (isCastSupported(callback)) {
                    ZWaveSupportedCommand command = supportedCommandParser
                            .parseCommand(ImmutableBuffer.overBuffer(callback.getCommandPayload()), callback.getSourceNodeId());
                    supportedCommandDispatcher.dispatchCommand(command);
                } else {
                    log.debug("Skipped {} frame: {}", callback.getRxStatus().getFrameCast(), BufferUtil.bufferToString(buffer));
                }
            } catch (FrameParseException e) {
                log.error("Failed to parse application command handler frame: {}", BufferUtil.bufferToString(buffer));
            }
        } else if (log.isDebugEnabled()) {
            log.debug("Skipped frame: {}", BufferUtil.bufferToString(buffer));
        }
    }

    private boolean isCastSupported(ApplicationCommandHandlerCallback callback) {
        FrameCast frameCast = callback.getRxStatus().getFrameCast();
        return frameCast == FrameCast.SINGLE
                || (frameCast == FrameCast.BROADCAST && supportBroadcasts)
                || (frameCast == FrameCast.MULTICAST && supportMulticasts);
    }
}
