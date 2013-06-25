package com.codahale.sfearthquakes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;

public class EarthquakeDb {
    private final ObjectMapper objectMapper;
    private final File rootDirectory;

    public EarthquakeDb(File rootDirectory) {
        this.objectMapper = new ObjectMapper();
        this.rootDirectory = rootDirectory;
    }

    public Optional<Earthquake> get(String id) throws IOException {
        final File file = dataFile(id, false);
        if (file.exists()) {
            return Optional.fromNullable(objectMapper.readValue(file, Earthquake.class));
        }
        return Optional.absent();
    }
    
    private File dataDirectory(String id) {
        final String prefix = id.substring(0, 6);
        return new File(rootDirectory, prefix);
    }
    
    private File dataFile(String id, boolean createDirectories) {
        final File dir = dataDirectory(id);
        if (createDirectories) {
            dir.mkdirs();
        }
        return new File(dir, id + ".json");
    }

    public void put(Earthquake earthquake) throws IOException {
        final File file = dataFile(earthquake.getId(), true);
        final ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(file, earthquake);
    }
}
