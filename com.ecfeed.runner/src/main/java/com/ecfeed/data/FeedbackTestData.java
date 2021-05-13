package com.ecfeed.data;

import org.json.JSONObject;

import java.util.Map;
import java.util.Optional;

public final class FeedbackTestData {

    private final FeedbackData feedbackData;
    private final String data;
    private final String id;

    private String status;

    private Optional<Map<String, String>> custom = Optional.empty();
    private Optional<Integer> duration = Optional.empty();
    private Optional<String> comment = Optional.empty();

    private boolean pending = true;

    private FeedbackTestData(FeedbackData feedbackData, String data, String id) {

        this.feedbackData = feedbackData;
        this.data = data;
        this.id = id;
    }

    static FeedbackTestData create(FeedbackData feedbackData, String data, String id) {

        return new FeedbackTestData(feedbackData, data, id);
    }

    public void addFeedback(String status) {

        if (pending) {
            this.pending = false;
            this.status = status;
            register();
        }
    }

    public void addFeedback(String status, int duration) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.duration = Optional.ofNullable(duration);
            register();
        }
    }

    public void addFeedback(String status, String comment) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.comment = Optional.ofNullable(comment);
            register();
        }
    }

    public void addFeedback(String status, Map<String, String> custom) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.custom = Optional.ofNullable(custom);
            register();
        }
    }

    public void addFeedback(String status, int duration, String comment) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.duration = Optional.ofNullable(duration);
            this.comment = Optional.ofNullable(comment);
            register();
        }
    }

    public void addFeedback(String status, int duration, Map<String, String> custom) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.duration = Optional.ofNullable(duration);
            this.custom = Optional.ofNullable(custom);
            register();
        }
    }

    public void addFeedback(String status, String comment, Map<String, String> custom) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.comment = Optional.ofNullable(comment);
            this.custom = Optional.ofNullable(custom);
            register();
        }
    }

    public void addFeedback(String status, int duration, String comment, Map<String, String> custom) {

        if (pending) {
            this.pending = false;
            this.status = status;
            this.duration = Optional.ofNullable(duration);
            this.comment = Optional.ofNullable(comment);
            this.custom = Optional.ofNullable(custom);
            register();
        }
    }

    private void register() {

        this.feedbackData.addFeedback(this.id, toJSONObject());
    }

    private JSONObject toJSONObject() {

        JSONObject json = new JSONObject();

        json.put("status", status);
        json.put("data", data);

        if (duration.isPresent()) {
            json.put("duration", duration.get());
        }

        if (comment.isPresent()) {
            json.put("comment", comment.get());
        }

        if (custom.isPresent()) {
            json.put("custom", custom);
        }

        return json;
    }
}
