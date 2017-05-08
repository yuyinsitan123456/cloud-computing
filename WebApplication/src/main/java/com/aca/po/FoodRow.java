package com.aca.po;

public class FoodRow {

    private int ogcFid = 0;
    private int totalTweets = 0;
    private int relevantTweets = 0;
    private int positiveTweets = 0;
    private int employees = 0;

    public int getOgcFid() {
        return ogcFid;
    }

    public void setOgcFid(int ogcFid) {
        this.ogcFid = ogcFid;
    }

    public int getTotalTweets() {
        return totalTweets;
    }

    public void setTotalTweets(int totalTweets) {
        this.totalTweets = totalTweets;
    }

    public int getRelevantTweets() {
        return relevantTweets;
    }

    public void setRelevantTweets(int relevantTweets) {
        this.relevantTweets = relevantTweets;
    }

    public int getPositiveTweets() {
        return positiveTweets;
    }

    public void setPositiveTweets(int positiveTweets) {
        this.positiveTweets = positiveTweets;
    }

    public int getEmployees() {
        return employees;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }

}
