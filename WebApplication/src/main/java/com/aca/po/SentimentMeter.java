package com.aca.po;

public class SentimentMeter {
    private double sydneyRatio;
    private int sydneyTotal;
    private int sydneyPositive;
    private double melbourneRatio;
    private int melbourneTotal;
    private int melbournePositive;

    public double getSydneyRatio() {
        return sydneyRatio;
    }

    public void setSydneyRatio(double sydneyRatio) {
        this.sydneyRatio = sydneyRatio;
    }

    public double getMelbourneRatio() {
        return melbourneRatio;
    }

    public void setMelbourneRatio(double melbourneRatio) {
        this.melbourneRatio = melbourneRatio;
    }

    public int getSydneyTotal() {
        return sydneyTotal;
    }

    public void setSydneyTotal(int sydneyTotal) {
        this.sydneyTotal = sydneyTotal;
    }

    public int getSydneyPositive() {
        return sydneyPositive;
    }

    public void setSydneyPositive(int sydneyPositive) {
        this.sydneyPositive = sydneyPositive;
    }

    public int getMelbourneTotal() {
        return melbourneTotal;
    }

    public void setMelbourneTotal(int melbourneTotal) {
        this.melbourneTotal = melbourneTotal;
    }

    public int getMelbournePositive() {
        return melbournePositive;
    }

    public void setMelbournePositive(int melbournePositive) {
        this.melbournePositive = melbournePositive;
    }
}
