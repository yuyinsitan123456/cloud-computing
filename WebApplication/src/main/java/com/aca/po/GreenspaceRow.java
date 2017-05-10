package com.aca.po;

public class GreenspaceRow {
    private String greenspaceId;
    private double totalTweets;
    private double positiveTweets;
    private String greenspaceName;

    public String getGreenspaceName() {
        return greenspaceName;
    }

    public void setGreenspaceName(String greenspaceName) {
        this.greenspaceName = greenspaceName;
    }

    public String getGreenspaceId() {
        return greenspaceId;
    }

    public void setGreenspaceId(String greenspaceId) {
        this.greenspaceId = greenspaceId;
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
