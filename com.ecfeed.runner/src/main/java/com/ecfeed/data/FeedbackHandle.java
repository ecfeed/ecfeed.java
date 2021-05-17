package com.ecfeed.data;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class FeedbackHandle {

    private final String id;
    private final String data;
    private final SessionData sessionData;

    private String status;

    private String comment = "";
    private Integer duration = -1;
    private Map<String, String> custom = new HashMap<>();

    private boolean pending = true;

    private FeedbackHandle(SessionData sessionData, String data, String id) {

        this.sessionData = sessionData;
        this.data = data;
        this.id = id;
    }

    static FeedbackHandle create(SessionData feedback, String data, String id) {

        return new FeedbackHandle(feedback, data, id);
    }

    public void addFeedback(boolean status) {

        parse(status, -1, null, null);
    }

    public void addFeedback(boolean status, int duration) {

        parse(status, duration, null, null);
    }

    public void addFeedback(boolean status, String comment) {

        parse(status, -1, comment, null);
    }

    public void addFeedback(boolean status, Map<String, String> custom) {

        parse(status, -1, null, custom);
    }

    public void addFeedback(boolean status, int duration, String comment) {

        parse(status, duration, comment, null);
    }

    public void addFeedback(boolean status, int duration, Map<String, String> custom) {

        parse(status, duration, null, custom);
    }

    public void addFeedback(boolean status, String comment, Map<String, String> custom) {

        parse(status, -1, comment, custom);
    }

    public void addFeedback(boolean status, int duration, String comment, Map<String, String> custom) {

        parse(status, duration, comment, custom);
    }

    private void parse(boolean status, int duration, String comment, Map<String, String> custom) {

        if (isDuplicate()) {
            return;
        }

        parseStatus(status);
        parseDuration(duration);
        parseComment(comment);
        parseCustom(custom);

        register();
    }

    private void parseStatus(boolean status) {

        this.status = status ? "P" : "F";
    }

    private void parseDuration(int duration) {

        if (duration >= 0) {
            this.duration = duration;
        }
    }

    private void parseComment(String comment) {

        if (comment != null && !comment.equalsIgnoreCase("")) {
            this.comment = comment;
        }
    }

    private void parseCustom(Map<String, String> custom) {

        if (custom != null && custom.size() > 0) {
            this.custom = custom;
        }
    }

    private boolean isDuplicate() {

        if (this.pending) {
            this.pending = true;
            return false;
        }

        return true;
    }

    private String register() {
        JSONObject data = toJSONObject();

        this.sessionData.feedbackHandleRegister(this.id, data);

        return data.toString();
    }

    private JSONObject toJSONObject() {

        JSONObject json = new JSONObject();

        json.put("status", status);
        json.put("data", data);

        if (duration >= 0) {
            json.put("duration", duration);
        }

        if (comment != null && !comment.equalsIgnoreCase("")) {
            json.put("comment", comment);
        }

        if (custom != null && custom.size() > 0) {
            json.put("custom", custom);
        }

        return json;
    }

    @Override
    public String toString() {

        return toJSONObject().toString();
    }
}
