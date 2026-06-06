package com.github.darksoulq.ner.util;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import net.kyori.adventure.text.Component;

public class ProbabilityParser {
    public static Component parseProbability(String expression) {
        if (expression == null || expression.isBlank()) return null;
        String clean = expression.trim();
        if (clean.matches("^([<>~]?\\s*\\d+(\\.\\d+)?|\\d+(\\.\\d+)?\\s*-\\s*\\d+(\\.\\d+)?)(%)?$")) {
            if (!clean.endsWith("%")) clean += "%";
        }
        return TextUtil.parse("<!italic><gray>Chance: <white>" + clean);
    }

    public static Component parseProbability(float probability) {
        String formatted = String.valueOf(probability);
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        return parseProbability(formatted + "%");
    }
}