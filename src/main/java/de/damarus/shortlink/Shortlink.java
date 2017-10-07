package de.damarus.shortlink;

import de.damarus.shortlink.files.RuleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Shortlink extends JFrame {

    public static String lastClipboard;
    private static LinkQuickwindow displayWindow;

    public static void main(String[] args) {
        try {
            RuleManager.loadAllRulesFromDisk();
        } catch (IOException e) {
            System.err.println("Could not access rules/ directory, exiting...");
            e.printStackTrace();
            System.exit(-1);
        }

        // Continuously check clipboard for changes, ignore known unproblematic errors
        while (true) {
            try {
                String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (!clipboard.equals(lastClipboard)) {
                    if (isLink(clipboard)) {
                        System.out.println("Handling link: " + clipboard);
                        ModifieableLink link = ModifieableLink.fromURL(new URL(clipboard));

                        // Don't be annoying when the link can't be changed anyways
                        if (link.isActuallyModifieable()) {
                            String beforeRules = link.toString();
                            RuleManager.applyRulesTo(link);

                            // Display the window if we can modify parameters, or the path changed
                            if (!link.getParameters().isEmpty() || !beforeRules.equals(link.toString())) {
                                getWindow().displayLink(link);
                            }
                        }
                    }

                    lastClipboard = clipboard;
                }

                Thread.sleep(1000 / 5);
            } catch (UnsupportedFlavorException | IllegalStateException ignored) {
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isLink(String clipboard) {
        try {
            new URL(clipboard);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static LinkQuickwindow getWindow() {
        if (displayWindow == null) displayWindow = new LinkQuickwindow();
        return displayWindow;
    }
}
