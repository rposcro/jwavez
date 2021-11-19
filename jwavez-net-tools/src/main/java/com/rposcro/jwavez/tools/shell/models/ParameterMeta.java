package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterMeta {

    private int number;
    private String memo;
    private int sizeInBits;

    @JsonIgnore
    public int getSizeInBytes() {
        return (this.sizeInBits / 8) + (this.sizeInBits % 8 == 0 ? 0 : 1);
    }
}
