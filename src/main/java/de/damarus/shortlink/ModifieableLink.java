package de.damarus.shortlink;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModifieableLink {

    private URL sourceUrl;

    private String protocol;
    private String host;
    private int port = -1;
    private String fragment;

    private List<UrlPathSegment> pathSegments = new ArrayList<>();
    private List<UrlParameter> params = new ArrayList<>();
    private boolean fragmentEnabled = true;

    private ModifieableLink() {}

    public static ModifieableLink fromURL(URL url) {
        if (url == null) throw new NullPointerException();

        ModifieableLink result = new ModifieableLink();
        result.sourceUrl = url;
        result.protocol = url.getProtocol();
        result.host = url.getHost();
        result.port = url.getPort();
        result.fragment = url.getRef();

        // Save all path segments separately
        if (url.getPath() != null) {
            Path p = Paths.get(url.getPath());
            for (int i = 0; i < p.getNameCount(); i++) {
                result.addPathSegment("/" + p.getName(i), true);
            }
        }

        // Split the query into a hashmap of parameter name and value
        if (url.getQuery() != null) {
            String[] params = url.getQuery().split("&");
            for (String para : params) {
                String[] keyValue = para.split("=");

                // Keys with no value get saved with a null as value
                if (keyValue.length == 2) {
                    result.addParameter(keyValue[0], keyValue[1], false);
                } else {
                    result.addParameter(keyValue[0], null, false);
                }
            }
        }

        return result;
    }

    public void addPathSegment(String segment, boolean enabled) {
        pathSegments.add(new UrlPathSegment(segment, enabled));
    }

    public void addParameter(String key, String value, boolean enabled) {
        params.add(new UrlParameter(key, value, enabled));
    }

    @Override
    public String toString() {
        StringBuilder link = new StringBuilder();
        link.append(protocol);
        link.append("://");
        link.append(host);

        if (port != -1) {
            link.append(":");
            link.append(port);
        }

        link.append(buildFullPath());

        StringBuilder query = new StringBuilder();
        for (UrlParameter param : params) {
            if (!param.enabled) continue;

            if (query.toString().isEmpty()) {
                query.append("?");
            } else {
                query.append("&");
            }

            query.append(param.key);

            if (param.value != null) {
                query.append("=");
                query.append(param.value);
            }
        }

        link.append(query);

        if (fragment != null && fragmentEnabled) {
            link.append("#");
            link.append(fragment);
        }
        return link.toString();
    }

    private String buildFullPath() {
        StringBuilder path = new StringBuilder();
        for (UrlPathSegment seg : pathSegments) {
            if (seg.isEnabled()) path.append(seg.toString());
        }

        if (path.toString().isEmpty()) path.append("/");

        return path.toString();
    }

    public boolean isActuallyModifieable() {
        return !params.isEmpty() || !pathSegments.isEmpty() || hasFragment();
    }

    public boolean hasFragment() {
        return fragment != null;
    }

    public boolean isFragmentEnabled() {
        return fragmentEnabled;
    }

    public void setFragmentEnabled(boolean fragmentEnabled) {
        this.fragmentEnabled = fragmentEnabled;
    }

    public List<UrlPathSegment> getPathSegments() {
        return pathSegments;
    }

    public Optional<UrlParameter> getParameter(String keyName) {
        return params.stream().filter(param -> param.key.equalsIgnoreCase(keyName)).findFirst();
    }

    public List<UrlParameter> getParameters() {
        return params;
    }

    public URL getSource() {
        return sourceUrl;
    }

    public static class UrlPathSegment {

        private String segment;
        private boolean enabled;

        private UrlPathSegment(String segment, boolean enabled) {
            if (!segment.startsWith("/")) throw new IllegalArgumentException("Paths must start with a '/'");

            this.segment = segment;
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return segment;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class UrlParameter {

        private String key;
        private String value;
        private boolean enabled;

        private UrlParameter(String key, String value, boolean enabled) {
            this.key = key;
            this.value = value;
            this.enabled = enabled;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
