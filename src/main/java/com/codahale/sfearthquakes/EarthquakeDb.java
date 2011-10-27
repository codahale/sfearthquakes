package com.codahale.sfearthquakes;

import com.google.common.base.Optional;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;

import java.io.File;
import java.io.IOException;

public class EarthquakeDb {
    private static final MappingJsonFactory FACTORY = new MappingJsonFactory();

    private final File rootDirectory;

    public EarthquakeDb(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public Optional<Earthquake> get(String id) throws IOException {
        final File file = dataFile(id, false);
        if (file.exists()) {
            return Optional.fromNullable(FACTORY.createJsonParser(file).readValueAs(Earthquake.class));
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
        final JsonGenerator generator = FACTORY.createJsonGenerator(file, JsonEncoding.UTF8);
        generator.writeObject(earthquake);
        generator.close();
    }
}
