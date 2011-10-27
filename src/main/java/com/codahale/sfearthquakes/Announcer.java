package com.codahale.sfearthquakes;

import twitter4j.*;
import twitter4j.auth.AccessToken;

public class Announcer {
    private final Twitter twitter;

    public Announcer(String consumerKey,
                     String consumerSecret,
                     String accessToken,
                     String accessSecret) {
        this.twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessSecret));
    }

    public void tweet(Announcement announcement) throws TwitterException {
        final StatusUpdate update = new StatusUpdate(announcement.toString());
        final GeoLocation location = new GeoLocation(announcement.getLatitude(),
                                                     announcement.getLongitude());
        update.setLocation(location);
        update.setDisplayCoordinates(true);
        twitter.updateStatus(update);
    }
}
