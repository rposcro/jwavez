package com.rposcro.jwavez.tools.shell.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongleInformation {

    private DongleNetworkInformation dongleNetworkInformation;
    private DongleRoleInformation dongleRoleInformation;
    private DongleDeviceInformation dongleDeviceInformation;
    private DongleCommandInformation dongleCommandInformation;
}