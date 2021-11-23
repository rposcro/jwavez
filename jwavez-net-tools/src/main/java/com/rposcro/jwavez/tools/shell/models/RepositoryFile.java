package com.rposcro.jwavez.tools.shell.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryFile {

    private String repositoryName;
    private DongleDeviceInformation relatedDongleDevice;
    private List<NodeInformation> nodes;
}
