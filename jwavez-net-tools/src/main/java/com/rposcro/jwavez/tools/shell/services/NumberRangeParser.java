package com.rposcro.jwavez.tools.shell.services;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class NumberRangeParser {

    public static final Pattern PATTERN_SINGLE = Pattern.compile("[0-9]+");
    public static final Pattern PATTERN_RANGE = Pattern.compile("[0-9]+-[0-9]+");
    public static final Pattern PATTERN_LIST = Pattern.compile("[0-9]+(?:,[0-9]+)+");

    public int[] parseNumberRange(String argument) throws ParseException {
        if (PATTERN_LIST.matcher(argument).matches()) {
            return parseAsList(argument);
        } else if (PATTERN_RANGE.matcher(argument).matches()) {
            return parseAsRange(argument);
        } else if (PATTERN_SINGLE.matcher(argument).matches()) {
            return parseAsSingle(argument);
        }
        throw new ParseException("Incorrect parameter number argument: " + argument, -1);

    }

    private int[] parseAsList(String optionValue) {
        return Stream.of(optionValue.split(","))
                .sequential()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private int[] parseAsRange(String optionValue) {
        String[] tokens = optionValue.split("-");
        int from  = Integer.parseInt(tokens[0]);
        int to = Integer.parseInt(tokens[1]);
        boolean swap = from > to;
        if (swap) {
            return IntStream.rangeClosed(to, from)
                    .sequential()
                    .map(num -> (from + to - num))
                    .toArray();
        } else {
            return IntStream.rangeClosed(from, to)
                    .sequential()
                    .toArray();
        }
    }

    private int[] parseAsSingle(String optionValue) {
        return new int[] {  Integer.parseInt(optionValue) };
    }
}
