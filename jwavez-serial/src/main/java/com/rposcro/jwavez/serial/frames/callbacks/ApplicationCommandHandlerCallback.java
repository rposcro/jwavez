package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;
import static java.lang.String.format;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.RxStatus;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@CallbackFrameModel(function = SerialCommand.APPLICATION_COMMAND_HANDLER)
public class ApplicationCommandHandlerCallback extends ZWaveCallback {

    private RxStatus rxStatus;
    private int rxRssi;
    private NodeId sourceNodeId;
    private int commandLength;
    private byte[] commandPayload;

    public ApplicationCommandHandlerCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.rxStatus = new RxStatus(frameBuffer.next());
        this.sourceNodeId = NodeId.forId(frameBuffer.next());
        this.commandLength = frameBuffer.nextUnsignedByte();
        this.commandPayload = frameBuffer.cloneRemainingBytes(commandLength);
        if (isRxRssiPresent(frameBuffer, commandLength)) {
            frameBuffer.skip(commandLength);
            this.rxRssi = frameBuffer.nextUnsignedWord();
        }
    }

    public String asFineString() {
        if (rxRssi != 0) {
            return String.format("APPLICATION_COMMAND_HANDLER(%02x) rxStatus(%02x) srcNode(%02x) CmdLen(%02x) Payload[%s] RxRSSI(%04x)",
                    SerialCommand.APPLICATION_COMMAND_HANDLER.getCode(),
                    rxStatus.getStatusValue(),
                    sourceNodeId.getId(),
                    commandLength,
                    IntStream.range(0, commandPayload.length)
                            .mapToObj(idx -> format("%02x", commandPayload[idx]))
                            .collect(Collectors.joining(" ")),
                    rxRssi
            );
        } else {
            return String.format("APPLICATION_COMMAND_HANDLER(%02x) rxStatus(%02x) srcNode(%02x) CmdLen(%02x) Payload[%s]",
                    SerialCommand.APPLICATION_COMMAND_HANDLER.getCode(),
                    rxStatus.getStatusValue(),
                    sourceNodeId.getId(),
                    commandLength,
                    IntStream.range(0, commandPayload.length)
                            .mapToObj(idx -> format("%02x", commandPayload[idx]))
                            .collect(Collectors.joining(" "))
            );
        }
    }

    private boolean isRxRssiPresent(ImmutableBuffer buffer, int commandLength) {
        // 1 for serial frame CRC, 2 min for rx rssi
        return buffer.available() >= (commandLength + 1 + 2);
    }

    public static void main(String[] args) {
        //byte[] bytes = {0x01, 0x0f, 0x00, 0x04, 0x00, 0x0a, 0x07, 0x60, 0x0d, 0x02, 0x02, 0x20, 0x01, 0x00, (byte) 0xb3, 0x00, 0x06};
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        Stream.of("01 0c 00 04 00 05 06 33 04 00 99 99 00 c3".split("\\s"))
                .mapToInt(numStr -> Integer.parseInt(numStr, 16))
                .forEach(byteBuffer::write);
        ImmutableBuffer buffer = ImmutableBuffer.overBuffer(byteBuffer.toByteArray());
        System.out.println(new ApplicationCommandHandlerCallback(buffer).asFineString());
    }
}
