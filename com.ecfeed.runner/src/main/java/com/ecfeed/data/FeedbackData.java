package com.ecfeed.data;

import org.json.JSONObject;

public final class FeedbackData {

    private final SessionData sessionData;

    private JSONObject testResults = new JSONObject();

    private boolean completed = false;
    private int testCasesTotal = 0;
    private int testCasesParsed = 0;

    private FeedbackData(SessionData sessionData) {

        this.sessionData = sessionData;
    }

    public static FeedbackData create(SessionData sessionData) {

        return new FeedbackData(sessionData);
    }

    public FeedbackTestData createFeedback(String data) {

        return FeedbackTestData.create(this, data, "0:" + testCasesTotal++);
    }

    public void addFeedback(String id, JSONObject feedback) {
        testCasesParsed++;

        testResults.put(id, feedback);

        if (testCasesParsed == testCasesTotal && completed) {
            processFeedback();
        }
    }

    public void finishTransmission() {
        completed = true;

        if (testCasesParsed == testCasesTotal) {
            processFeedback();
        }
    }

    private void processFeedback() {

        System.out.println("send");
    }

    private JSONObject toJSONObject() {

        return testResults;
    }
}
