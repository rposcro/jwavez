package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeDetails;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.NodeDetailsCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import com.rposcro.jwavez.tools.shell.services.NumberRangeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.text.ParseException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeParameterCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeDetailsCache nodeDetailsCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NumberRangeParser numberRangeParser;

    @ShellMethod(value = "Shows parameter value", key = { "param-get", "pg" })
    public String showCurrentNodeId(String paramNumbersRange) {
        try {
            int[] paramNumbers = numberRangeParser.parseNumberRange(paramNumbersRange);
            return "";
        } catch(ParseException e) {
            return "Cannot parse argument: " + paramNumbersRange;
        }
    }
}
