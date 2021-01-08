package com.castle.util.regex;

import com.castle.annotations.Stateless;

import java.util.regex.Pattern;

@Stateless
public class Patterns {

    private Patterns() {
    }

    public static Pattern wrapWithWildcards(String regex) {
        return Pattern.compile(String.format(".*%s.*", regex));
    }

    public static Pattern wrapWithWildcards(Pattern pattern) {
        return Pattern.compile(String.format(".*%s.*", pattern.pattern()),
                pattern.flags());
    }
}
