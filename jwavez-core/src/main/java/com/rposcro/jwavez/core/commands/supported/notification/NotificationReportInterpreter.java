package com.rposcro.jwavez.core.commands.supported.notification;

import com.rposcro.jwavez.core.model.NotificationType;

public class NotificationReportInterpreter {

    private NotificationReport notificationReport;

    public NotificationReportInterpreter(NotificationReport notificationReport) {
        this.notificationReport = notificationReport;
    }

    public NotificationType decodeNotificationType() {
        return NotificationType.ofCodeOptional(notificationReport.getNotificationType()).orElse(null);
    }
}
