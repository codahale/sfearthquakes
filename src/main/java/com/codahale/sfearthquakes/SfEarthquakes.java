package com.codahale.sfearthquakes;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SfEarthquakes {
    private static Logger LOGGER = LoggerFactory.getLogger(SfEarthquakes.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("java -jar sfearthquakes.jar <config file> <data directory>");
            System.exit(-1);
        } else {
            final boolean noTweeting = args.length > 2 && "--silent".equals(args[2]);

            final Configuration configuration = new Configuration(new File(args[0]));
            final Announcer announcer = new Announcer(configuration.getTwitterConsumerKey(),
                                                      configuration.getTwitterConsumerSecret(),
                                                      configuration.getTwitterAccessToken(),
                                                      configuration.getTwitterAccessSecret());
            final EarthquakeList earthquakes = EarthquakeList.load();
            final EarthquakeDb db = new EarthquakeDb(new File(args[1]));
            for (Earthquake earthquake : earthquakes) {
                if (earthquake.isBayArea()) {
                    final Optional<Earthquake> existing = db.get(earthquake.getId());
                    if (!existing.isPresent()) {
                        final Announcement announcement = new Announcement(earthquake);
                        if (earthquake.isPerceivable()) {
                            if (noTweeting) {
                                LOGGER.warn("NOT tweeting new earthquake: {}", announcement);
                            } else {
                                LOGGER.warn("Tweeting new earthquake: {}", announcement);
                                announcer.tweet(announcement);
                            }
                        } else {
                            LOGGER.info("NOT tweeting boring new earthquake: {}", announcement);
                        }
                        db.put(earthquake);
                    } else {
                        LOGGER.debug("Already seen {}", earthquake.getId());
                    }
                } else {
                    LOGGER.debug("Non-local earthquake: {}", earthquake.getId());
                }
            }
        }
    }

    private SfEarthquakes() { /* singleton */ }
}
