package com.home.twitter;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by praveen on 1/26/18.
 */
@IgnoreExtraProperties
public class Tweet {
    public String tweetBy;
    public String tweet;

    public Tweet() {
    }

    public Tweet(String tweetBy, String tweet) {
        this.tweetBy = tweetBy;
        this.tweet = tweet;
    }

    @Exclude
    public String getTweetBy() {
        return tweetBy;
    }

    @Exclude
    public void setTweetBy(String tweetBy) {
        this.tweetBy = tweetBy;
    }

    @Exclude
    public String getTweet() {
        return tweet;
    }

    @Exclude
    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

}