package de.damarus.shortlink.files;

import java.util.ArrayList;
import java.util.List;

public class RuleFile {

    private String match;

    /**
     * Include these elements in the shortened URL. Should be matched against path elements and GET parameters.
     */
    private List<String> include = new ArrayList<>();

    /**
     * Exclude these elements from the shortened URL. Should be matched against path elements and GET parameters.
     * Exclude statements override includes.
     */
    private List<String> exclude = new ArrayList<>();

    public String getPattern() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }
}
