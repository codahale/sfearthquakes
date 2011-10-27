package com.codahale.sfearthquakes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class Configuration {
    private final Properties properties;

    public Configuration(File configFile) throws IOException {
        this.properties = new Properties();
        final FileInputStream input = new FileInputStream(configFile);
        properties.load(input);
        input.close();
    }

    public String getTwitterConsumerKey() {
        return checkNotNull(properties.getProperty("twitter.consumerKey"));
    }

    public String getTwitterConsumerSecret() {
        return checkNotNull(properties.getProperty("twitter.consumerSecret"));
    }

    public String getTwitterAccessToken() {
        return checkNotNull(properties.getProperty("twitter.accessToken"));
    }

    public String getTwitterAccessSecret() {
        return checkNotNull(properties.getProperty("twitter.accessSecret"));
    }
}
