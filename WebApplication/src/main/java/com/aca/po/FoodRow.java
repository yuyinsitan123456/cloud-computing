package com.aca.po;

public class FoodRow {

    private int ogcFid = 0;
    private double totalTweets = 0;
    private double relevantTweets = 0;
    private double positiveTweets = 0;
    private double employees = 0;

    public int getOgcFid() {
        return ogcFid;
    }

    public void setOgcFid(int ogcFid) {
        this.ogcFid = ogcFid;
    }

    public double getTotalTweets() {
        return totalTweets;
    }

    public void setTotalTweets(double totalTweets) {
        this.totalTweets = totalTweets;
    }

    public double getRelevantTweets() {
        return relevantTweets;
    }

    public void setRelevantTweets(double relevantTweets) {
        this.relevantTweets = relevantTweets;
    }

    public double getPositiveTweets() {
        return positiveTweets;
    }

    public void setPositiveTweets(double positiveTweets) {
        this.positiveTweets = positiveTweets;
    }

    public double getEmployees() {
        return employees;
    }

    public void setEmployees(double employees) {
        this.employees = employees;
    }

}
