package de.damarus.shortlink;

import de.damarus.shortlink.files.RuleManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ShorteningTests {

    public static final String[][] links = new String[][]{
            {
                    "https://www.google.de/search?newwindow=1&source=hp&ei=0PT0WbKZCsug6AT7tK_YBw&q=hallo+welt&oq=hallo+welt&gs_l=psy-ab.3..0i203k1l10.660.1881.0.1934.11.8.0.0.0.0.190.708.0j5.5.0....0...1.1.64.psy-ab..6.5.706.0..0j46j35i39k1j0i67k1j0i46k1.0.EwPp3_z6tsk",
                    "https://www.google.de/search?q=hallo+welt"
            },
            {
                    "https://images-ext-1.discordapp.net/external/D55JyWnTPnWw0AQJ3XvLJdftHWrWWMMljA6DrGcg4ow/https/cdn.discordapp.com/avatars/176612606444830720/f727cd56769797d8f6fceb36fed5cbea.webp?width=18&height=18",
                    "https://images-ext-1.discordapp.net/external/D55JyWnTPnWw0AQJ3XvLJdftHWrWWMMljA6DrGcg4ow/https/cdn.discordapp.com/avatars/176612606444830720/f727cd56769797d8f6fceb36fed5cbea.webp"
            },
            {
                    "https://www.youtube.com/watch?v=gd6vFwFeJMY&list=FLFB3guteUc1lCJmnf0Crclg&index=4",
                    "https://www.youtube.com/watch?v=gd6vFwFeJMY"
            },
            {
                    "https://www.amazon.de/Comply-Memory-Schaum-Kopfh%C3%B6rer-Ohrst%C3%B6psel-Schwarz/dp/B002NUJ2RM/ref=sr_1_2?ie=UTF8&qid=1509294034&sr=8-2&keywords=comply",
                    "https://www.amazon.de/dp/B002NUJ2RM"
            }
    };

    @BeforeClass
    public static void setup() throws IOException {
        RuleManager.loadAllRulesFromDisk(false);
    }

    @Test
    public void CheckExampleLinks() throws MalformedURLException {
        for (String[] link : links) {
            System.out.print(String.format("\rChecking link: %s", link[0]));

            ModifieableLink ml = ModifieableLink.fromURL(new URL(link[0]));
            ml = RuleManager.applyRulesTo(ml);
            assertEquals(link[1], ml.toString());
        }

        System.out.println("\r ");
    }
}
