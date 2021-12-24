package com.rposcro.jwavez.tools.shell.models;

import com.rposcro.jwavez.serial.model.LibraryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
