package com.ecfeed;

import com.ecfeed.session.dto.DataSession;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains feedback data.
 */
public final class TestHandle {

    private final String id;
    private final String data;
    private final DataSession dataSession;

    private String status;

    private String comment = "";
    private Integer duration = -1;
    private Map<String, String> custom = new HashMap<>();

    private boolean pending = true;

    private TestHandle(DataSession dataSession, String data, String id) {

        this.dataSession = dataSession;
        this.data = data;
        this.id = id;
    }

    /**
     * Creates a TestHandle object.
     *
     * @param feedback  the DataSession object
     * @param data      a string containing parsed data
     * @param id        the session id
     * @return          a new TestHandle object.
     */
    @Deprecated
    public static TestHandle create(DataSession feedback, String data, String id) {

        return new TestHandle(feedback, data, id);
    }

    /**
     * Adds a custom property.
     *
     * @param key       the key of the property
     * @param value     the name of the property
     * @return          the current TestHandle object (can be used for chaining)
     */
    public TestHandle addProperty(String key, String value) {

        this.custom.put(key, value);

        return this;
    }

    /**
     * Adds a comment.
     *
     * @param comment   a custom comment
     * @return          the current TestHandle object (can be used for chaining)
     */
    public TestHandle addComment(String comment) {

        parseComment(comment);

        return this;
    }

    /**
     * Adds the duration of the test.
     *
     * @param duration  the duration of the test
     * @return          the current TestHandle object (can be used for chaining)
     */
    public TestHandle addDuration(int duration) {

        parseDuration(duration);

        return this;
    }

    /**
     * Adds the status of the test.
     *
     * @param status    the outcome of the test (passed/failed)
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status) {

        parse(status, -1, null, null);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param duration  the duration of the test
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, int duration) {

        parse(status, duration, null, null);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param comment   a custom comment
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, String comment) {

        parse(status, -1, comment, null);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param custom    a custom map of key-value pairs
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, Map<String, String> custom) {

        parse(status, -1, null, custom);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param duration  the duration of the test
     * @param comment   a custom comment
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, int duration, String comment) {

        parse(status, duration, comment, null);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param duration  the duration of the test
     * @param custom    a custom map of key-value pairs
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, int duration, Map<String, String> custom) {

        parse(status, duration, null, custom);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param comment   a custom comment
     * @param custom    a custom map of key-value pairs
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, String comment, Map<String, String> custom) {

        parse(status, -1, comment, custom);

        return response();
    }

    /**
     * Adds feedback data.
     *
     * @param status    the outcome of the test (passed/failed)
     * @param duration  the duration of the test
     * @param comment   a custom comment
     * @param custom    a custom map of key-value pairs
     * @return          a text describing the test outcome
     */
    public String addFeedback(boolean status, int duration, String comment, Map<String, String> custom) {

        parse(status, duration, comment, custom);

        return response();
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

    private String response() {

        if (!this.comment.equalsIgnoreCase("")) {
            return this.comment;
        }

        if (this.status.equalsIgnoreCase("P")) {
            return "Passed";
        } else {
            return "Failed";
        }
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
        var dataSessionFacade = Factory.getDataSessionFacade(dataSession);

        JSONObject data = toJSONObject();

        dataSessionFacade.feedbackHandleRegister(this.id, data);

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
