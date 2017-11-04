package de.damarus.shortlink;

import de.damarus.shortlink.files.RuleManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ShorteningTests {

    public static final String[][] links = new String[][]{{
            // amazon-global.rule
            "https://www.amazon.co.uk/gp/yourstore/home/ref=nav_cs_ys",
            "https://www.amazon.co.uk/gp/yourstore/home"
    }, {
            // amazon-product.rule
            "https://www.amazon.de/Comply-Memory-Schaum-Kopfh%C3%B6rer-Ohrst%C3%B6psel-Schwarz/dp/B002NUJ2RM/ref=sr_1_2?ie=UTF8&qid=1509294034&sr=8-2&keywords=comply",
            "https://www.amazon.de/dp/B002NUJ2RM"
    }, {    // amazon-search.rule
            "https://www.amazon.de/s/ref=nb_sb_noss_2?__mk_de_DE=%C3%85M%C3%85%C5%BD%C3%95%C3%91&url=search-alias%3Daps&field-keywords=foobar&rh=i%3Aaps%2Ck%3Afoobar",
            "https://www.amazon.de/s?field-keywords=foobar"
    }, {     // google-search.rule
            "https://www.google.de/search?newwindow=1&source=hp&ei=0PT0WbKZCsug6AT7tK_YBw&q=hallo+welt&oq=hallo+welt&gs_l=psy-ab.3..0i203k1l10.660.1881.0.1934.11.8.0.0.0.0.190.708.0j5.5.0....0...1.1.64.psy-ab..6.5.706.0..0j46j35i39k1j0i67k1j0i46k1.0.EwPp3_z6tsk",
            "https://www.google.de/search?q=hallo+welt"
    }, {    // reddit-comments.rule
            "https://www.reddit.com/r/gaming/comments/2u3lo7/one_of_the_best_things_to_see_in_a_game_after/co521li/?context=10000",
            "https://www.reddit.com/r/gaming/comments/2u3lo7/one_of_the_best_things_to_see_in_a_game_after/co521li?context=10000"
    }, {    // youtube-playlist.rule
            "https://www.youtube.com/watch?v=gd6vFwFeJMY&list=FLFB3guteUc1lCJmnf0Crclg&index=4",
            "https://www.youtube.com/watch?v=gd6vFwFeJMY"
    }, {    // youtube-watch.rule
            "https://www.youtube.com/watch?v=H8Z4e0muPS8&t=0s",
            "https://www.youtube.com/watch?v=H8Z4e0muPS8"
    }, {    // random link
            "https://images-ext-1.discordapp.net/external/D55JyWnTPnWw0AQJ3XvLJdftHWrWWMMljA6DrGcg4ow/https/cdn.discordapp.com/avatars/176612606444830720/f727cd56769797d8f6fceb36fed5cbea.webp?width=18&height=18",
            "https://images-ext-1.discordapp.net/external/D55JyWnTPnWw0AQJ3XvLJdftHWrWWMMljA6DrGcg4ow/https/cdn.discordapp.com/avatars/176612606444830720/f727cd56769797d8f6fceb36fed5cbea.webp"
    }};

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
