package com.rposcro.jwavez.tools.cli.options.node;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.CommandOptions;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Getter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

@Getter
public class NodeConfigurationReadOptions extends AbstractNodeBasedOptions {

    private static final String OPT_PARAM_NUMS = "pn";

    public static final Options OPTIONS = CommandOptions.defaultNodeBasedOptions()
            .addOption(Option.builder(OPT_PARAM_NUMS)
                    .longOpt("parameter-numbers")
                    .required()
                    .hasArg()
                    .argName("params")
                    .desc("param id(s) whose values to read").build());
    public static final Pattern PATTERN_SINGLE = Pattern.compile("[0-9]+");
    public static final Pattern PATTERN_RANGE = Pattern.compile("[0-9]+-[0-9]+");
    public static final Pattern PATTERN_LIST = Pattern.compile("[0-9]+(?:,[0-9]+)+");

    @Getter
    private int[] parameterNumbers;

    public NodeConfigurationReadOptions(String[] args) throws CommandOptionsException {
        super(OPTIONS, args);
        String optionValue = commandLine.getOptionValue(OPT_PARAM_NUMS);
        parameterNumbers = findParameterProcessor(optionValue).apply(optionValue).toArray();
    }

    private Function<String, IntStream> findParameterProcessor(String optionValue) throws CommandOptionsException {
        if (PATTERN_LIST.matcher(optionValue).matches()) {
            return this::parseAsList;
        } else if (PATTERN_RANGE.matcher(optionValue).matches()) {
            return this::parseAsRange;
        } else if (PATTERN_SINGLE.matcher(optionValue).matches()) {
            return this::parseAsSingle;
        }
        throw new CommandOptionsException("Incorrect parameter number option: '%s'", optionValue);
    }

    private IntStream parseAsList(String optionValue) {
        return Stream.of(optionValue.split(",")).sequential().mapToInt(Integer::parseInt);
    }

    private IntStream parseAsRange(String optionValue) {
        String[] tokens = optionValue.split("-");
        int from = Integer.parseInt(tokens[0]);
        int to = Integer.parseInt(tokens[1]);
        boolean swap = from > to;
        if (swap) {
            return IntStream.rangeClosed(to, from).sequential().map(num -> (from + to - num));
        } else {
            return IntStream.rangeClosed(from, to).sequential();
        }
    }

    private IntStream parseAsSingle(String optionValue) {
        return IntStream.of(Integer.parseInt(optionValue)).sequential();
    }
}
