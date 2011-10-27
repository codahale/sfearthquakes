package com.codahale.sfearthquakes;

import com.google.common.base.Optional;

import java.io.File;
import java.util.logging.Logger;

public class SfEarthquakes {
    private static Logger LOGGER = Logger.getLogger(SfEarthquakes.class.getCanonicalName());

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
                final Optional<Earthquake> existing = db.get(earthquake.getId());
                if (!existing.isPresent()) {
                    final Announcement announcement = new Announcement(earthquake);
                    if (announcement.isTweetable()) {
                        if (noTweeting) {
                            LOGGER.warning("NOT tweeting new earthquake: " + earthquake);
                        } else {
                            LOGGER.warning("Tweeting new earthquake: " + earthquake);
                            announcer.tweet(announcement);
                        }
                    } else {
                        LOGGER.info("Uninteresting new earthquake: " + earthquake);
                    }
                    db.put(earthquake);
                } else {
                    LOGGER.fine("Already seen " + earthquake.getId());
                }
            }
        }
    }

    private SfEarthquakes() { /* singleton */ }
}
