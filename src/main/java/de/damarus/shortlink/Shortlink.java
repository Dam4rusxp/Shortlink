package de.damarus.shortlink;

import de.damarus.shortlink.files.RuleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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

        while (true) {
            try {
                String clipboard = getStringClipboard();
                if (clipboard != null && !clipboard.equals(lastClipboard)) {
                    lastClipboard = clipboard;

                    if (isLink(clipboard)) {
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

    private static String getStringClipboard() {
        DataFlavor[] currentFlavors = Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors();
        if (Arrays.stream(currentFlavors).anyMatch(DataFlavor::isFlavorTextType)) {
            try {
                return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static void handleNewLink(String clipboard) throws MalformedURLException {
        ModifieableLink link = ModifieableLink.fromURL(new URL(clipboard));

        // Don't be annoying when the link can't be changed anyways
        if (link.isActuallyModifieable()) {
            String beforeRules = link.toString();
            RuleManager.applyRulesTo(link);

            // Display the window if we can modify parameters, or the path changed
            // That means we don't find fragments annoying enough to show the window
            if (!link.getParameters().isEmpty() || !beforeRules.equals(link.toString())) {
                getWindow().displayLink(link);
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
