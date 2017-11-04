package de.damarus.shortlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlPattern {

    // Regex for finding variables in a supplied pattern
    private static Pattern variablePattern = Pattern.compile("\\{([\\w\\d]+)}");

    public static Map<String, String> extractFirstValid(String input, String[] pattern) {
        for (String p : pattern) {
            Map<String, String> result = matchAndExtract(input, p);

            if (result != null) return result;
        }

        return null;
    }

    public static Map<String, String> matchAndExtract(String input, String pattern) {
        HashMap<String, String> resultMap = new HashMap<>();

        // Expand wildcards to regex syntax
        pattern = "^" + pattern + "$";
        pattern = pattern.replaceAll("\\.", "\\\\.");
        pattern = pattern.replaceAll("([^\\\\])\\?", "$1.??");
        pattern = pattern.replaceAll("([^\\\\])\\*", "$1.*?");

        // Determine variable names
        List<String> varNames = new ArrayList<>();
        Matcher varMatcher = variablePattern.matcher(pattern);
        while (varMatcher.find()) {
            varNames.add(varMatcher.group().replaceAll("[{}]", ""));
        }

        // Final regex, ready for parsing input
        String finalPattern = varMatcher.replaceAll("(?<$1>.*?)");
        Matcher inputMatcher = Pattern.compile(finalPattern).matcher(input);

        if (!inputMatcher.matches()) return null;

        // Grab all defined variables from the matched input
        for (String name : varNames) {
            resultMap.put(name, inputMatcher.group(name));
        }

        return resultMap;
    }
}
