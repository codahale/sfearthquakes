package com.codahale.sfearthquakes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EarthquakeList extends ForwardingList<Earthquake> {
    private static final Logger LOGGER = Logger.getLogger(EarthquakeList.class.getCanonicalName());
    private static final URI EARTHQUAKES_URI = URI.create("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.csv");

    public static EarthquakeList load() throws Exception {
        try {
            final CsvMapper mapper = new CsvMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            final CsvSchema schema = CsvSchema.builder()
                                              .setUseHeader(true)
                                              .addColumn("time")
                                              .addColumn("latitude", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("longitude", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("depth", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("mag", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("magType")
                                              .addColumn("nst", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("gap", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("dmin", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("rms", CsvSchema.ColumnType.NUMBER)
                                              .addColumn("net")
                                              .addColumn("id")
                                              .addColumn("updated")
                                              .addColumn("place")
                                              .build();

            final ObjectReader reader = mapper.reader(Earthquake.class).with(schema);
            return new EarthquakeList(reader.<Earthquake>readValues(EARTHQUAKES_URI.toURL()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load USGS site", e);
            throw e;
        }
    }

    private final ImmutableList<Earthquake> earthquakes;

    private EarthquakeList(Iterator<Earthquake> earthquakes) {
        this.earthquakes = ImmutableList.copyOf(earthquakes);
    }

    @Override
    protected List<Earthquake> delegate() {
        return earthquakes;
    }
}
