package com.codahale.sfearthquakes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.net.URI;

public class Earthquake {
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
        return (latitude <= 38.20 && latitude >= 37.30) &&
                (longitude <= -121.87 && longitude >= -123.00);
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
