package com.rposcro.jwavez.tools.shell.models;

import com.rposcro.jwavez.core.classes.CommandClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandClassMeta {

    private CommandClass commandClass;
    private int version;
}
