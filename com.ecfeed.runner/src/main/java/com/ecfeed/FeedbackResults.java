package com.ecfeed;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackResults {

    Map<String, FeedbackResult> feedbackResults = new HashMap<>();

    public void FeedbackResults() {

        this.feedbackResults = new HashMap<>();
    }

    public void addResult(String id, FeedbackResult feedbackResult) {

        this.feedbackResults.put(id, feedbackResult);
        int size = feedbackResults.size();
        System.out.println(size);
    }

    public JSONObject createJsonObject() {

        JSONObject jsonObject =  new JSONObject(this.feedbackResults);
        String text = jsonObject.toString();
        System.out.println(text);

        return jsonObject;
    }
}
