package com.github.darksoulq.ner.util;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import net.kyori.adventure.text.Component;
import java.util.regex.Pattern;

public class ProbabilityParser {
    private static final Pattern PROBABILITY_PATTERN = Pattern.compile("^([<>~]?\\s*\\d+(\\.\\d+)?|\\d+(\\.\\d+)?\\s*-\\s*\\d+(\\.\\d+)?)(%)?$");

    public static Component parseProbability(String expression) {
        if (expression == null || expression.isBlank()) return null;

        String clean = expression.trim();
        if (PROBABILITY_PATTERN.matcher(clean).matches() && !clean.endsWith("%")) {
            clean += "%";
        }

        return TextUtil.parse("<!italic><gray>Chance: <white>" + clean);
    }

    public static Component parseProbability(float probability) {
        String formatted = (probability == (int) probability) ? String.valueOf((int) probability) : String.valueOf(probability);
        return parseProbability(formatted + "%");
    }
}