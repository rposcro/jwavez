package com.rposcro.jwavez.tools.shell.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongleNvmData {

    private int manufacturerId;
    private int productType;
    private int productId;
    private short appVersion;
    private short appRevision;
    private short chipType;
    private short chipVersion;
    private byte[] nvmBytes;
}
