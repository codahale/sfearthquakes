package com.codahale.sfearthquakes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.net.URI;

import static java.lang.Math.*;

public class Earthquake {
    private static final double SF_LAT = 37.77493;
    private static final double SF_LONG = -122.41942;

    private double latitude;
    private double longitude;
    private double magnitude;
    private String id;
    private String place;

    @JsonProperty
    public double getLatitude() {
        return latitude;
    }

    @JsonProperty
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty
    public double getLongitude() {
        return longitude;
    }

    @JsonProperty
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("mag")
    public double getMagnitude() {
        return magnitude;
    }

    @JsonProperty("mag")
    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getPlace() {
        return place;
    }

    @JsonProperty
    public void setPlace(String place) {
        this.place = place;
    }

    @JsonIgnore
    public URI getURI() {
        return URI.create("http://earthquake.usgs.gov/earthquakes/eventpage/" + id);
    }

    @JsonIgnore
    public boolean isPerceivable() {
        return magnitude >= 3.0;
    }

    @JsonIgnore
    public boolean isBayArea() {
        return distance(SF_LAT, SF_LONG, latitude, longitude) <= 100; // miles
    }

    private double distance(double lat1, double long1, double lat2, double long2) {
        final double r = 3958.75;
        final double a = pow(sin(toRadians(lat2 - lat1) / 2), 2) +
                         pow(sin(toRadians(long2 - long1) / 2), 2) *
                         cos(toRadians(lat1)) *
                         cos(toRadians(lat2));
        return r * 2 * atan2(sqrt(a), sqrt(1 - a));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("magnitude", magnitude)
                      .add("place", place)
                      .add("longitude", longitude)
                      .add("latitude", latitude)
                      .toString();
    }
}
