package de.damarus.shortlink.files;

import java.util.ArrayList;
import java.util.List;

public class RuleFile {

    private String[] match;
    private String[] matchHost;
    private String[] matchPath;
    private String[] matchQuery;

    /**
     * Include these elements in the shortened URL. Should be matched against path elements and GET parameters.
     */
    private List<String> include = new ArrayList<>();

    /**
     * Exclude these elements from the shortened URL. Should be matched against path elements and GET parameters.
     * Exclude statements override includes.
     */
    private List<String> exclude = new ArrayList<>();

    private String replace;

    public String[] getMatch() {
        return match;
    }

    public void setMatch(String[] match) {
        this.match = match;
    }

    public String[] getMatchHost() {
        return matchHost;
    }

    public void setMatchHost(String[] matchHost) {
        this.matchHost = matchHost;
    }

    public String[] getMatchPath() {
        return matchPath;
    }

    public void setMatchPath(String[] matchPath) {
        this.matchPath = matchPath;
    }

    public String[] getMatchQuery() {
        return matchQuery;
    }

    public void setMatchQuery(String[] matchQuery) {
        this.matchQuery = matchQuery;
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

    public String getReplace() {
        return replace;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }
}
