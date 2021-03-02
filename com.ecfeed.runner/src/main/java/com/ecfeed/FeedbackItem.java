package com.ecfeed;

import java.util.HashMap;
import java.util.Map;

public class FeedbackItem {

    private final Map<String, String> custom;
    private final String data;

    private boolean success;
    private String comment;
    private int duration;

    FeedbackItem(String data) {
        this.data = data;
        this.success = true;
        this.custom = new HashMap<>();
    }

    public Map<String, String> getCustom() {
        return custom;
    }

    public String getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getComment() {
        return comment;
    }

    public int getDuration() {
        return duration;
    }

    FeedbackItem setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    FeedbackItem setComment(String comment) {
        this.comment = comment;
        return this;
    }

    FeedbackItem setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    FeedbackItem addCustom(String key, String value) {
        custom.put(key, value);
        return this;
    }

}
