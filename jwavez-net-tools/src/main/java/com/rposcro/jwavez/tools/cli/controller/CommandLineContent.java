package com.rposcro.jwavez.tools.cli.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommandLineContent {

    private CommandTreeNode commandNode;
    private String[] commandOtions;
}
