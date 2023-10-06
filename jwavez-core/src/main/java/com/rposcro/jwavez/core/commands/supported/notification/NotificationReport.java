package com.rposcro.jwavez.core.commands.supported.notification;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.NotificationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NotificationReport extends ZWaveSupportedCommand<NotificationCommandType> {

    private byte alarmTypeV1;
    private byte alarmLevelV1;
    private byte notificationStatus;
    private byte notificationType;
    private byte notificationEvent;

    private boolean sequencePresent;
    private byte sequenceNumber;
    private byte[] eventParameters;

    public NotificationReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(NotificationCommandType.NOTIFICATION_REPORT, sourceNodeId);
        payload.skip(2);
        this.alarmTypeV1 = payload.nextByte();
        this.alarmLevelV1 = payload.nextByte();
        payload.skip(1); // reserved
        this.notificationStatus = payload.nextByte(); //ff
        this.notificationType = payload.nextByte(); //06
        this.notificationEvent = payload.nextByte(); //17 v 16

        int control = payload.nextUnsignedByte();
        int parametersNumber = control & 0x1f;

        this.sequencePresent = (control & 0x80) != 0;
        this.eventParameters = payload.cloneRemainingBytes(parametersNumber);

        if (sequencePresent) {
            payload.skip(parametersNumber);
            this.sequenceNumber = payload.nextByte();
        }

        // 00 00 00 ff 06 17 00
        // 00 00 00 ff 06 16 00
    }

    public NotificationReportInterpreter interpreter() {
        return new NotificationReportInterpreter(this);
    }

    @Override
    public String asNiceString() {
        return String.format("%s alarmTypeV1(%02x) alarmLevelV1(%02x) notificationStatus(%02x) notificationType(%02x)" +
                        " notificationEvent(%02x) sequencePresent(%s), sequenceNumber(%02x) eventParameters[%s]",
                super.asNiceString(),
                alarmTypeV1,
                alarmLevelV1,
                notificationStatus,
                notificationType,
                notificationEvent,
                sequencePresent,
                sequenceNumber,
                BuffersUtil.asString(eventParameters));
    }
}
