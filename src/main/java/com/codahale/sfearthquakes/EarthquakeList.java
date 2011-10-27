package com.codahale.sfearthquakes;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static java.lang.Double.parseDouble;

public class EarthquakeList extends ForwardingList<Earthquake> {
    private static final URI EARTHQUAKES_URI = URI.create("http://earthquake.usgs.gov");
    private static final URI ROOT_URI = EARTHQUAKES_URI.resolve("/earthquakes/recenteqscanv/FaultMaps/");
    private static final URI MAP_URI = ROOT_URI.resolve("./San_Francisco_eqs.html");

    public static EarthquakeList load() throws IOException {
        return parse(Jsoup.connect(MAP_URI.toASCIIString()).get());
    }

    public static EarthquakeList parse(Document map) {
        final ImmutableList.Builder<Earthquake> earthquakes = ImmutableList.builder();
        for (Element earthquake : map.select("table.tabular tr")) {
            if (earthquake.select("th abbr").isEmpty()) {
                final double magnitude = parseDouble(earthquake.select("td.magnitude").text());
                final Element link = earthquake.select("td.location a").first();
                final URI uri = EARTHQUAKES_URI.resolve(link.attr("href"));
                final double latitude = parseGeo(earthquake.select("td.latitude").text());
                final double longitude = parseGeo(earthquake.select("td.longitude").text());
                final String location = link.text();
                earthquakes.add(new Earthquake(magnitude, uri, location, latitude, longitude));
            }
        }
        return new EarthquakeList(earthquakes.build());
    }
    
    private static double parseGeo(String s) {
        final double offset = s.endsWith("S") || s.endsWith("W") ? -1 : 1;
        return parseDouble(s.substring(0, s.length() - 2)) * offset;
    }

    private final ImmutableList<Earthquake> earthquakes;

    private EarthquakeList(Iterable<Earthquake> earthquakes) {
        this.earthquakes = ImmutableList.copyOf(earthquakes);
    }

    @Override
    protected List<Earthquake> delegate() {
        return earthquakes;
    }
}
