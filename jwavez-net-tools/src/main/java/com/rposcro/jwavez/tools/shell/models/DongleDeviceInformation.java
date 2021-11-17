package com.rposcro.jwavez.tools.shell.models;

import com.rposcro.jwavez.serial.model.LibraryType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DongleDeviceInformation {

    private int manufacturerId;
    private int productType;
    private int productId;
    private short appVersion;
    private short appRevision;
    private short chipType;
    private short chipVersion;
    private short version;
    private short capabilities;
    private LibraryType libraryType;
    private String versionResponse;
    private short dataResponse;
    private int[] serialCommandIds;
}
