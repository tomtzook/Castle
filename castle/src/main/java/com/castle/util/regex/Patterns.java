package com.castle.util.regex;

import java.util.regex.Pattern;

public class Patterns {

    private Patterns() {}

    public static Pattern wrapWithWildcards(String regex) {
        return Pattern.compile(String.format(".*%s.*", regex));
    }

    public static Pattern wrapWithWildcards(Pattern pattern) {
        return Pattern.compile(String.format(".*%s.*", pattern.pattern()),
                pattern.flags());
    }
}
