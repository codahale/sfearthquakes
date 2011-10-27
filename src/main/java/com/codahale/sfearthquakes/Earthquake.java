package com.codahale.sfearthquakes;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

public class Earthquake {
    private static final Pattern ID_PATTERN = Pattern.compile("^.*/(.*)\\.html$");

    private final String id;
    private final double magnitude;
    private final URI uri;
    private final String location;
    private final double longitude, latitude;

    @JsonCreator
    public Earthquake(@JsonProperty("magnitude") double magnitude,
                      @JsonProperty("uri") URI uri,
                      @JsonProperty("location") String location,
                      @JsonProperty("latitude") double latitude,
                      @JsonProperty("longitude") double longitude) {
        this.id = parseId(uri);
        this.magnitude = magnitude;
        this.uri = uri;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonProperty
    public double getMagnitude() {
        return magnitude;
    }

    @JsonProperty
    public URI getURI() {
        return uri;
    }

    @JsonProperty
    public String getLocation() {
        return location;
    }

    @JsonProperty
    public double getLongitude() {
        return longitude;
    }

    @JsonProperty
    public double getLatitude() {
        return latitude;
    }

    private String parseId(URI uri) {
        final Matcher matcher = ID_PATTERN.matcher(uri.getPath());
        checkArgument(matcher.matches(), "%s doesn't match %s", uri, ID_PATTERN);
        return matcher.group(1);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("magnitude", magnitude)
                      .add("uri", uri)
                      .add("location", location)
                      .add("longitude", longitude)
                      .add("latitude", latitude)
                      .toString();
    }
}
