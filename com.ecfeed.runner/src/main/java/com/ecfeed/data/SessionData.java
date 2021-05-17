package com.ecfeed.data;

import com.ecfeed.Config;
import com.ecfeed.TypeExport;
import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SessionData {

    private final Feedback feedback = Feedback.create(this);

    private final Connection connection;
    private final String generatorType;
    private final String method;
    private final String model;

    private Map<String, Object> generatorOptions = new HashMap<>();
    private Map<String, String> custom = new HashMap<>();
    private JSONObject testResults = new JSONObject();
    private TypeExport template = null;
    private String methodNameQualified = "";
    private String testSessionId = "";
    private String testSessionLabel = "";
    private Object constraints = "ALL";
    private Object testSuites = "ALL";
    private Object choices = "ALL";
    private int timestamp = -1;

    private SessionData(Connection connection, String model, String method, String generatorType) {
        this.connection = connection;
        this.model = model;
        this.method = method;
        this.generatorType = generatorType;
    }

    public static SessionData create(Connection connection, String model, String method, String generatorType) {

        return new SessionData(connection, model, method, generatorType);
    }

    public String generateURLForTestData() {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForTestDataCore(requestBuilder);
        generateURLForTestDataParameters(requestBuilder);

        return requestBuilder.toString();
    }

    private StringBuilder generateURLForTestDataCore(StringBuilder builder) {

        return builder.append(getHttpAddress()).append("/").append(Config.Key.urlService);
    }

    private StringBuilder generateURLForTestDataParameters(StringBuilder builder) {
        String type = getTemplate().isPresent() ? Config.Value.parRequestTypeExport : Config.Value.parRequestTypeStream;

        builder.append("?");
        builder.append(Config.Key.parRequestType).append("=").append(type);
        builder.append("&");
        builder.append(Config.Key.parClient).append("=").append(Config.Value.parClient);
        builder.append("&");
        builder.append(Config.Key.parRequest).append("=").append(generateURLForTestDataRequest());

        return builder;
    }

    public String generateBodyForTestData() {

        return "";
    }

    public String generateURLForFeedback() {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForFeedbackCore(requestBuilder);
        generateURLForFeedbackParameters(requestBuilder);

        return requestBuilder.toString();
    }

    private StringBuilder generateURLForFeedbackCore(StringBuilder builder) {

        return builder.append(getHttpAddress()).append("/").append(Config.Key.urlFeedback);
    }

    private StringBuilder generateURLForFeedbackParameters(StringBuilder builder) {

        return builder;
    }

    public String generateBodyForFeedback() {
        JSONObject json = new JSONObject();

        generateFeedbackBodyElement(json, "modelId", getModel());
        generateFeedbackBodyElement(json, "methodInfo", getMethodNameQualified());
        generateFeedbackBodyElement(json, "testSessionId", getTestSessionId());
        generateFeedbackBodyElement(json, "testSessionLabel", getTestSessionLabel());
        generateFeedbackBodyElement(json, "framework", Config.Value.parClient);
        generateFeedbackBodyElement(json, "timestamp", getTimestamp());
        generateFeedbackBodyElement(json, "custom", getCustom());
        generateFeedbackBodyElement(json, "testSuites", getTestSuites());
        generateFeedbackBodyElement(json, "constraints", getConstraints());
        generateFeedbackBodyElement(json, "choices", getChoices());
        generateFeedbackBodyElement(json, "testResults", getTestResults());
        generateFeedbackBodyElement(json, "generatorType", getGeneratorType());
        generateFeedbackBodyElement(json, "generatorOptions", getGeneratorOptions().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ")));

        return json.toString();
    }

    private void generateFeedbackBodyElement(JSONObject json, String key, Object value) {

        if (value == null) {
            return;
        }

        if (value instanceof String) {
            if (value.toString().equalsIgnoreCase("") || value.toString().equalsIgnoreCase("ALL")) {
                return;
            }
        }

        if (value instanceof Map<?,?>) {
            if (((Map<?, ?>) value).size() == 0) {
                return;
            }
        }

        if (value instanceof Collection<?>) {
            if (((Collection<?>) value).size() == 0) {
                return;
            }
        }

        json.put(key, value);
    }

    private String generateURLForTestDataRequest() {
        JSONObject request = new JSONObject();

        request.put(Config.Key.parModel, getModel());
        request.put(Config.Key.parMethod, getMethodName());
        request.put(Config.Key.parUserData, generateURLForTestDataRequestUserData());

        getTemplate().ifPresent(e -> request.put(Config.Key.parTemplate, e));

        try {
            return URLEncoder.encode(request.toString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The request could not be generated.");
        }
    }

    private String generateURLForTestDataRequestUserData() {
        JSONObject requestUserData = new JSONObject();

        requestUserData.put(Config.Key.parDataSource, getGeneratorType());
        requestUserData.put(Config.Key.parProperties, getGeneratorOptions());

        return requestUserData.toString().replaceAll("\"", "'");
    }

    private String getHttpAddress() {
        String httpAddress = connection.getHttpAddress();

        if (httpAddress == null) {
            throw new RuntimeException("The generator address is not defined");
        }

        if (!httpAddress.startsWith("https://")) {
            throw new RuntimeException("The generator address should start with http://");
        }

        return httpAddress;
    }

    private Optional<TypeExport> getTemplate() {

        if (this.template == null || this.template.equals(TypeExport.Raw)) {
            return Optional.empty();
        }

        return Optional.of(template);
    }

    private String getMethodName() {

        return method;
    }

    private String getMethodNameQualified() {

        return methodNameQualified;
    }

    public void setMethodNameQualified(String methodNameQualified) {

        this.methodNameQualified = methodNameQualified;
    }

    private String getModel() {

        return model;
    }

    private String getGeneratorType() {

        return generatorType;
    }

    private Map<String, Object> getGeneratorOptions() {

        return generatorOptions;
    }

    public void setGeneratorOptions(Map<String, Object> properties) {
        this.generatorOptions = new HashMap<>();

        properties.entrySet().stream().forEach(e -> {
            if (e.getKey().equalsIgnoreCase(Config.Key.parConstraints)) {
                setConstraints(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parTestSuites)) {
                setTestSuites(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parChoices)) {
                setChoices(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parTestSessionLabel)) {
                setTestSessionLabel(e.getValue().toString());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parCustom)) {
                setCustom((Map<String, String>)e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parFeedback)) {
                if (e.getValue().equals(true) || e.getValue().toString().equalsIgnoreCase("true")) {
                    this.feedback.enable();
                }
            } else {
                this.generatorOptions.put(e.getKey(), e.getValue());
            }
        });

    }

    public String getTestSessionId() {

        return this.testSessionId;
    }

    public void setTestSessionId(String testSessionId) {

        this.testSessionId = testSessionId;
    }

    public String getTestSessionLabel() {

        return this.testSessionLabel;
    }

    public void setTestSessionLabel(String testSessionLabel) {

        this.testSessionLabel = testSessionLabel;
    }

    public int getTimestamp() {

        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {

        this.timestamp = timestamp;
    }

    public void setTemplate(TypeExport template) {

        this.template = template;
    }

    public Map<String, String> getCustom() {

        return custom;
    }

    public void setCustom(Map<String, String> custom) {

        this.custom = custom;
    }

    public Object getConstraints() {

        return constraints;
    }

    public void setConstraints(Object constraints) {

        this.constraints = constraints;
    }

    public Object getTestSuites() {

        return testSuites;
    }

    public void setTestSuites(Object testSuites) {

        this.testSuites = testSuites;
    }

    public Object getChoices() {

        return choices;
    }

    public void setChoices(Object choices) {

        this.choices = choices;
    }

    public JSONObject getTestResults() {
        return testResults;
    }

//-------------------------------------------------------------------------------------

    public HttpClient getHttpClient() {
        return connection.getHttpClient();
    }

    public void transmissionFinished() {

        feedback.complete();
    }

    public Optional<FeedbackHandle> createFeedbackHandle(String data) {

        return feedback.createFeedbackHandle(data);
    }

    void registerFeedbackHandle(String id, JSONObject feedback) {

        testResults.put(id, feedback);
    }


}
