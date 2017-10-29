package de.damarus.shortlink.front;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.damarus.shortlink.ModifieableLink;
import de.damarus.shortlink.ModifieableLink.UrlParameter;
import de.damarus.shortlink.files.RuleManager;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.net.URL;

import static de.damarus.shortlink.Util.getStringClipboard;
import static de.damarus.shortlink.Util.isLink;

public class ShortlinkCLI {

    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();
        parser.accepts("l", "Link").withRequiredArg().ofType(String.class);
        parser.accepts("c", "Use clipboard");

        OptionSet options = parser.parse(args);
        try {
            makeRun(options);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
            parser.printHelpOn(System.out);
        }
    }

    public static void makeRun(OptionSet options) {
        String link = null;
        if (options.has("l")) {
            link = (String) options.valueOf("l");
        } else if (options.has("c")) {
            link = getStringClipboard();
        }

        Preconditions.checkArgument(!Strings.isNullOrEmpty(link), "No link supplied");
        Preconditions.checkArgument(isLink(link), "Malformed link");
        System.out.println("Is valid link.\n");

        try {
            System.out.println("Loading rule files.\n");
            RuleManager.loadAllRulesFromDisk(true);
            ModifieableLink mLink = ModifieableLink.fromURL(new URL(link));
            RuleManager.applyRulesTo(mLink);

            System.out.println("Parameters:");

            for (UrlParameter param : mLink.getParameters()) {
                System.out.println(String.format("%10s=%s", param.getKey(), param.getValue()));
            }

            System.out.println("\nShortened link:");
            System.out.println(mLink.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
