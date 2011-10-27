package com.codahale.sfearthquakes;

import com.google.common.collect.ImmutableList;

import static com.google.common.primitives.Ints.min;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import static java.lang.String.format;

public class Announcement {
    private static final ImmutableList<String> PREFIXES = ImmutableList.of(
        "Meh.",       // > 0.0 Richter
        "Meh.",       // > 1.0 Richter
        "Meh.",       // > 2.0 Richter
        "Huh.",       // > 3.0 Richter
        "Hey.",       // > 4.0 Richter
        "Whoah.",     // > 5.0 Richter
        "DUDE.",      // > 6.0 Richter
        "HOLY CRAP.", // > 7.0 Richter
        "MY GOD.",    // > 8.0 Richter
        "GOODBYE."    // > 9.0 Richter
    );
    
    private static String prefix(double magnitude) {
        return PREFIXES.get(min(10, (int) round(floor(magnitude))));
    }

    private final Earthquake earthquake;

    public Announcement(Earthquake earthquake) {
        this.earthquake = earthquake;
    }

    public boolean isTweetable() {
        return earthquake.getMagnitude() >= 3.0;
    }

    @Override
    public String toString() {
        return format("%s A %1.1f quake just happened %s: %s",
                      prefix(earthquake.getMagnitude()), earthquake.getMagnitude(),
                      earthquake.getLocation(), earthquake.getURI());
    }

    public double getLongitude() {
        return earthquake.getLongitude();
    }

    public double getLatitude() {
        return earthquake.getLatitude();
    }
}
