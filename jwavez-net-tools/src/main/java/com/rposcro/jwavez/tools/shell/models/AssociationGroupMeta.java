package com.rposcro.jwavez.tools.shell.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociationGroupMeta {

    private int groupId;
    private String memo;
}
