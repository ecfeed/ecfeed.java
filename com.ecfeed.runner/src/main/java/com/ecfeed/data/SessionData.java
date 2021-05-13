package com.ecfeed.data;

import com.ecfeed.Config;
import com.ecfeed.TypeExport;
import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionData {

    private final static String FRAMEWORK = "Java";

    private final ConnectionData connectionData;

    private final String model;
    private final String method;
    private final String generatorType;

    private Map<String, Object> generatorOptions = new HashMap<>();

    private Optional<FeedbackData> feedbackData = Optional.empty();

    private String[] argumentTypes;
    private String[] argumentNames;

    private String methodNameQualified;
    private String testSessionId;
    private int timestamp;

    private Optional<Map<String, String>> custom = Optional.empty();
    private Optional<TypeExport> template = Optional.empty();
    private Optional<Object> constraints = Optional.empty();
    private Optional<Object> testSuites = Optional.empty();
    private Optional<Object> choices = Optional.empty();
    private Optional<String> testSessionLabel = Optional.empty();

    private SessionData(ConnectionData connectionData, String model, String method, String generatorType) {
        this.connectionData = connectionData;
        this.model = model;
        this.method = method;
        this.generatorType = generatorType;
    }

    public static SessionData create(ConnectionData connectionData, String model, String method, String generatorType) {
        return new SessionData(connectionData, model, method, generatorType);
    }

    public String getMethod() {
        return method;
    }

    public String getModel() {
        return model;
    }

    public String getGeneratorType() {
        return generatorType;
    }

    public String[] getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(String[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    public String[] getArgumentNames() {
        return argumentNames;
    }

    public void setArgumentNames(String[] argumentNames) {
        this.argumentNames = argumentNames;
    }

    public void setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setMethodNameQualified(String methodNameQualified) {
        this.methodNameQualified = methodNameQualified;
    }

    public Optional<TypeExport> getTemplate() {
        return template;
    }

    public void setTemplate(TypeExport template) {
        this.template = Optional.ofNullable(template);
    }

    public Map<String, Object> getProperties() {
        return generatorOptions;
    }

    public void setProperties(Map<String, Object> properties) {
        this.generatorOptions = new HashMap<>();

        properties.entrySet().stream().forEach(e -> {
            if (e.getKey().equalsIgnoreCase(Config.Key.parConstraints)) {
                this.constraints = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parTestSuites)) {
                this.testSuites = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parChoices)) {
                this.choices = Optional.of(e.getValue());
            } else if(e.getKey().equalsIgnoreCase(Config.Key.parFeedback)) {
                this.feedbackData = Optional.of(FeedbackData.create(this));
            } else {
                this.generatorOptions.put(e.getKey(), e.getValue());
            }
        });

    }

//-------------------------------------------------------------------------------------

    public String getGeneratorAddress() {
        return connectionData.getHttpAddress();
    }

    public HttpClient getHttpClient() {
        return connectionData.getHttpClient();
    }

    public Optional<FeedbackTestData> getFeedback(String data) {

        if (feedbackData.isPresent()) {
            return Optional.of(feedbackData.get().createFeedback(data));
        }

        return Optional.empty();
    }

    public void addFeedback(String id, JSONObject feedback) {

        if (feedbackData.isPresent()) {
            feedbackData.get().addFeedback(id, feedback);
        }
    }




}
