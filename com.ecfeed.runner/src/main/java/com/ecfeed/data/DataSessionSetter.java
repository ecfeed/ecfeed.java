package com.ecfeed.data;

import com.ecfeed.TestHandle;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Queue;

public interface DataSessionSetter {

    String generateURLForTestData(DataSession data);

    String generateURLForFeedback(DataSession data);

    String generateURLForTestDataRequest(DataSession data);

    String generateBodyForFeedback(DataSession data);

    String generateBodyForTestData(DataSession data);

    void feedbackSetComplete(DataSession data);

    void feedbackHandleRegister(DataSession data, String id, JSONObject feedback);

    Optional<TestHandle> feedbackHandleCreate(DataSession data, String handle);

    void activateStructure(DataSession data, String signature);

    Object[] getTestCase(DataSession data, Queue<String> arguments);
}
