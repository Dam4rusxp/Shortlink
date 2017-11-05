package de.damarus.shortlink.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.damarus.shortlink.ModifieableLink;
import de.damarus.shortlink.ModifieableLink.UrlParameter;
import de.damarus.shortlink.ModifieableLink.UrlPathSegment;
import de.damarus.shortlink.UrlPattern;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RuleManager {

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
        Files.write(target, json.getBytes(StandardCharsets.UTF_8));

        return target;
    }

    private static Gson getGson() {
        if (gson == null) gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson;
    }

    public static ModifieableLink applyRulesTo(ModifieableLink link) {
        // For every rulefile
        for (RuleFile rules : ruleFiles.values()) {
            Map<String, String> match = matchAll(link, rules);

            // If the link matches the pattern
            if (match != null) {

                if (rules.getReplace() == null) {
                    applyIncludeExclude(link, rules);
                } else {
                    try {
                        link = applyReplace(rules.getReplace(), match);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return link;
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

    public static Map<String, String> matchAll(ModifieableLink link, RuleFile ruleFile) {
        URL source = link.getSource();
        Map<String, String> urlMatch, hostMatch, pathMatch, queryMatch, combined;

        urlMatch = matchOne(source.toString(), ruleFile.getMatch());
        hostMatch = matchOne(source.getHost(), ruleFile.getMatchHost());
        pathMatch = matchOne(source.getPath(), ruleFile.getMatchPath());
        queryMatch = matchOne(source.getQuery(), ruleFile.getMatchQuery());

        // If something did not match, return null
        if (urlMatch == null || hostMatch == null || pathMatch == null || queryMatch == null) {
            return null;
        }

        combined = new HashMap<>();
        combined.putAll(urlMatch);
        combined.putAll(hostMatch);
        combined.putAll(pathMatch);
        combined.putAll(queryMatch);

        return combined;
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

        String json = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        Gson gson = getGson();

        RuleFile rule = gson.fromJson(json, RuleFile.class);
        Path filename = file.getFileName();
        if (rule != null && filename != null) ruleFiles.put(filename.toString(), rule);

        return rule;
    }

    private static ModifieableLink applyReplace(String replacementPattern, Map<String, String> vars) throws MalformedURLException {
        String target = replacementPattern;

        for (Entry<String, String> entry : vars.entrySet()) {
            target = target.replaceAll("\\{" + entry.getKey() + "}", entry.getValue());
        }

        URL newUrl = new URL(target);
        return ModifieableLink.fromURL(newUrl);
    }

    private static Map<String, String> matchOne(String input, String[] patterns) {
        if (patterns != null) {
            return UrlPattern.extractFirstValid(input, patterns);
        } else {
            return new HashMap<>();
        }
    }
}
