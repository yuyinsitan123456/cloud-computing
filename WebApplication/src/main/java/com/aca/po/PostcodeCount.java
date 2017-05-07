package com.aca.po;

import javafx.geometry.Pos;

public class PostcodeCount implements Comparable<PostcodeCount> {

    private String postcodeLabel;
    private int count;
    private int positiveCount;

    public String getPostcodeLabel() {
        return postcodeLabel;
    }

    public void setPostcodeLabel(String postcodeLabel) {
        this.postcodeLabel = postcodeLabel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(int positiveCount) {
        this.positiveCount = positiveCount;
    }

    @Override
    public int compareTo(PostcodeCount o) {
        return this.count - o.getCount();
    }
}
