package com.aca.po;

public class PostcodeRow {
    private String postcode;
    private double totalTweets;
    private double positiveTweets;

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public double getTotalTweets() {
        return totalTweets;
    }

    public void setTotalTweets(double totalTweets) {
        this.totalTweets = totalTweets;
    }

    public double getPositiveTweets() {
        return positiveTweets;
    }

    public void setPositiveTweets(double positiveTweets) {
        this.positiveTweets = positiveTweets;
    }

}
