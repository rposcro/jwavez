package com.rposcro.jwavez.tools.shell.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongleRoleInformation {

    private int dongleNodeId;
    private boolean isRealPrimary;
    private boolean isSecondary;
    private boolean isSUC;
    private boolean isSIS;
    private boolean isOnOtherNetwork;
}
