package com.ecfeed;

public class FeedbackResult {

    // {"data": "{\"testCase\":[{\"name\":\"choice11\",\"value\":\"V11\"},{\"name\":\"choice21\",\"value\":\"V21\"}]}", "status": "P", "duration": 1394},

    private String data; // "data": "{\"testCase\":[{\"name\":\"choice11\",\"value\":\"V11\"},{\"name\":\"choice21\",\"value\":\"V21\"}]}"
    private String status; // "status": "P"
    private long duration; // "duration": 1394 (milliseconds)

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(boolean isTestPassed) {

        if (isTestPassed) {
            this.status = "P";
        } else {
            this.status = "F";
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
