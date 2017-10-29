package de.damarus.shortlink.files;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.damarus.shortlink.ModifieableLink;
import de.damarus.shortlink.ModifieableLink.UrlParameter;
import de.damarus.shortlink.ModifieableLink.UrlPathSegment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleManager {

    public static ModifieableLink applyRulesTo(ModifieableLink link) {
        String sourceString = link.getSource().toString();

        // For every rulefile
        for (RuleFile rules : ruleFiles.values()) {
            // If the link matches the pattern
            if (sourceString.matches(rules.getPattern())) {

                if (Strings.isNullOrEmpty(rules.getReplace())) {
                    // For every query parameter of the link
                    applyIncludeExclude(link, rules);
                } else {
                    try {
                        link = applyReplace(link, rules);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return link;
    }

    private static Gson gson;
    private static HashMap<String, RuleFile> ruleFiles = new HashMap<>();

    /*** 
     * Saves the specified RuleFile to the rules/ subdir.
     *
     * @return The path to the saved rule or null if the rule does not exist.
     */
    public static Path saveRuleFile(String ruleName) throws IOException {
        if (!ruleFiles.containsKey(ruleName)) return null;

        if (!Files.exists(Paths.get("rules/"))) {
            Files.createDirectory(Paths.get("rules"));
        }

        Gson gson = getGson();
        String json = gson.toJson(ruleFiles.get(ruleName));
        Path target = Paths.get("rules", ruleName + ".rule");
        Files.write(target, json.getBytes());

        return target;
    }

    private static Gson getGson() {
        if (gson == null) gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson;
    }

    private static void applyIncludeExclude(ModifieableLink link, RuleFile rules) {
        for (UrlParameter param : link.getParameters()) {
            boolean include = param.isEnabled();

            // Check if a rule includes the parameter
            for (String incRule : rules.getInclude()) {
                // Skip if this is not a query parameter rule
                if (incRule.startsWith("/") || !incRule.contains("=")) continue;

                if (param.toString().startsWith(incRule)) {
                    include = true;
                    break;
                }
            }

            // Check if there is a rule excluding the parameter, which overrides include
            for (String excRule : rules.getExclude()) {
                // Skip if this is not a query parameter rule
                if (excRule.startsWith("/") || !excRule.contains("=")) continue;

                if (param.toString().startsWith(excRule)) {
                    include = false;
                    break;
                }
            }

            param.setEnabled(include);
        }

        // For every path segment in the link
        for (UrlPathSegment segment : link.getPathSegments()) {
            boolean include = segment.isEnabled();

            for (String excRule : rules.getExclude()) {
                // Skip if this is not a path segment rule
                if (!excRule.startsWith("/")) continue;

                if (segment.toString().startsWith(excRule)) {
                    include = false;
                    break;
                }
            }

            segment.setEnabled(include);
        }
    }

    private static ModifieableLink applyReplace(ModifieableLink link, RuleFile rules) throws MalformedURLException {
        Pattern pattern = Pattern.compile(rules.getPattern());
        Matcher matcher = pattern.matcher(link.getSource().toString());

        matcher.find();

        String newString = matcher.replaceAll(rules.getReplace());
        ModifieableLink newLink = ModifieableLink.fromURL(new URL(newString));

        return newLink;
    }

    public enum ShorteningMethod {
        /**
         * Don't use rules at all, and keep defaults
         */
        NONE,

        /**
         * Use include and exclude lists
         */
        SIMPLE,

        /**
         * Use capture groups and replacement string
         */
        REWRITE
    }

    public static void loadAllRulesFromDisk(boolean silent) throws IOException {
        Path dir = Paths.get("rules");
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }

        DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.rule");
        stream.forEach(p -> {
            try {
                loadRuleFile(p, silent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public static RuleFile loadRuleFile(Path file, boolean silent) throws IOException {
        if (file == null) throw new NullPointerException();
        if (!Files.exists(file)) throw new IOException("File does not exist");

        if (!silent) {
            System.out.println("Reading rule file: " + file.toString());
        }

        String json = new String(Files.readAllBytes(file));
        Gson gson = getGson();

        RuleFile rule = gson.fromJson(json, RuleFile.class);
        ruleFiles.put(file.getFileName().toString(), rule);

        return rule;
    }
}
