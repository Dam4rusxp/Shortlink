package de.damarus.shortlink.files;

import java.util.ArrayList;
import java.util.Collections;
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
        return match != null ? match.clone() : null;
    }

    public void setMatch(String[] match) {
        this.match = match.clone();
    }

    public String[] getMatchHost() {
        return matchHost != null ? matchHost.clone() : null;
    }

    public void setMatchHost(String[] matchHost) {
        this.matchHost = matchHost.clone();
    }

    public String[] getMatchPath() {
        return matchPath != null ? matchPath.clone() : null;
    }

    public void setMatchPath(String[] matchPath) {
        this.matchPath = matchPath.clone();
    }

    public String[] getMatchQuery() {
        return matchQuery != null ? matchQuery.clone() : null;
    }

    public void setMatchQuery(String[] matchQuery) {
        this.matchQuery = matchQuery.clone();
    }

    public List<String> getInclude() {
        return include != null ? Collections.unmodifiableList(include) : null;
    }

    public void setInclude(List<String> include) {
        this.include = new ArrayList<>(include);
    }

    public List<String> getExclude() {
        return exclude != null ? Collections.unmodifiableList(exclude) : null;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = new ArrayList<>(exclude);
    }

    public String getReplace() {
        return replace;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }
}
