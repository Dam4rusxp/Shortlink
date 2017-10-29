package de.damarus.shortlink;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Util {

    public static boolean isLink(String clipboard) {
        try {
            new URL(clipboard);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String getStringClipboard() {
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
}
