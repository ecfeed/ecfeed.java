package com.ecfeed.session;

import com.ecfeed.TestHandle;
import com.ecfeed.session.dto.DataSession;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Queue;

public interface DataSessionFacade {

    DataSession getDataSession();

    String generateURLForTestData();

    String generateURLForFeedback();

    String generateURLForTestDataRequest();

    String generateBodyForFeedback();

    void feedbackSetComplete();

    void feedbackHandleRegister(String id, JSONObject feedback);

    Optional<TestHandle> feedbackHandleCreate(String handle);

    void activateStructure(String signature);

    Object[] getTestCase(Queue<String> arguments);
}
