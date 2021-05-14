package com.ecfeed.data;

import org.json.JSONObject;

import java.util.Optional;

public final class Feedback {

    private final SessionData sessionData;

    private JSONObject testResults = new JSONObject();

    private boolean enabled = false;
    private boolean completed = false;
    private int testCasesTotal = 0;
    private int testCasesParsed = 0;

    private Feedback(SessionData sessionData) {

        this.sessionData = sessionData;
    }

    static Feedback create(SessionData sessionData) {

        return new Feedback(sessionData);
    }

    void enable() {

        this.enabled = true;
    }

    void complete() {
        completed = true;

        if (testCasesParsed == testCasesTotal) {
            sendFeedback();
        }
    }

    Optional<FeedbackHandle> createFeedbackHandle(String data) {

        if (!this.enabled) {
            return Optional.empty();
        }

        return Optional.of(FeedbackHandle.create(this, data, "0:" + testCasesTotal++));
    }

    void registerFeedbackHandle(String id, JSONObject feedback) {

        if (!this.enabled) {
            return;
        }

        testCasesParsed++;

        testResults.put(id, feedback);

        if (testCasesParsed == testCasesTotal && completed) {
            sendFeedback();
        }
    }

    private void sendFeedback() {

        if (!this.enabled) {
            return;
        }

        System.out.println("send");
    }

}
