package de.damarus.shortlink;

import de.damarus.shortlink.files.RuleManager;
import de.damarus.shortlink.front.LinkQuickwindow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Shortlink {

    public static String lastClipboard;

    public static void main(String[] args) {
        try {
            RuleManager.loadAllRulesFromDisk(false);
        } catch (IOException e) {
            System.err.println("Could not access rules/ directory, exiting...");
            e.printStackTrace();
            System.exit(-1);
        }

        while (true) {
            try {
                String clipboard = Util.getStringClipboard();
                if (clipboard != null && !clipboard.equals(lastClipboard)) {
                    lastClipboard = clipboard;

                    if (Util.isLink(clipboard)) {
                        System.out.println("Handling link: " + clipboard);
                        handleNewLink(clipboard);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(1000 / 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handleNewLink(String clipboard) throws MalformedURLException {
        ModifieableLink link = ModifieableLink.fromURL(new URL(clipboard));

        // Don't be annoying when the link can't be changed anyways
        if (link.isActuallyModifieable()) {
            link = RuleManager.applyRulesTo(link);

            // Display the window if we can modify parameters, or the link was changed otherwise
            // That means we don't find fragments annoying enough to show the window
            if (!link.getParameters().isEmpty() || !clipboard.equals(link.toString())) {
                // Old Swing window
                getWindow().displayLink(link);

                // New JavaFX window
//                Application.launch(QuickwindowApplication.class, String.valueOf(link));
            }
        }
    }

    public static LinkQuickwindow getWindow() {
        // We can't reuse the frames because Windows will temporarily block listeners when they reappear
        return new LinkQuickwindow();
    }
}
