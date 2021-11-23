package com.rposcro.jwavez.tools.shell.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongleCommandInformation {

    private int[] supportedSerialCommandIds;
}
