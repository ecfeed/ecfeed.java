package com.ecfeed.data;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.FeedbackHandle;
import com.ecfeed.type.TypeExport;
import com.ecfeed.helper.HelperConnection;
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

public class DataSession {

    private final JSONObject testResults = new JSONObject();

    private final DataConnection connection;
    private final String generatorType;
    private final String method;
    private final String model;

    private Map<String, Object> generatorOptions = new HashMap<>();
    private Map<String, String> custom = new HashMap<>();
    private TypeExport template = null;
    private String methodNameQualified = "";
    private String testSessionId = "";
    private String testSessionLabel = "";
    private Object constraints = ConfigDefault.Value.parAll;
    private Object testSuites = ConfigDefault.Value.parAll;
    private Object choices = ConfigDefault.Value.parAll;
    private int timestamp = -1;

    private boolean enabled = false;
    private boolean completed = false;
    private int testCasesTotal = 0;
    private int testCasesParsed = 0;

    private DataSession(DataConnection connection, String model, String method, String generatorType) {
        this.connection = connection;
        this.model = model;
        this.method = method;
        this.generatorType = generatorType;
    }

    public static DataSession create(DataConnection connection, String model, String method, String generatorType) {

        return new DataSession(connection, model, method, generatorType);
    }

    public String generateURLForTestData() {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForTestDataCore(requestBuilder);
        generateURLForTestDataParameters(requestBuilder);

        return requestBuilder.toString();
    }

    private void generateURLForTestDataCore(StringBuilder builder) {

        builder.append(getHttpAddress()).append("/").append(ConfigDefault.Key.urlService);
    }

    private void generateURLForTestDataParameters(StringBuilder builder) {
        String type = getTemplate().isPresent() ? ConfigDefault.Value.parRequestTypeExport : ConfigDefault.Value.parRequestTypeStream;

        builder.append("?");
        builder.append(ConfigDefault.Key.reqDataRequestType).append("=").append(type);
        builder.append("&");
        builder.append(ConfigDefault.Key.reqDataClient).append("=").append(ConfigDefault.Value.parClient);
        builder.append("&");
        builder.append(ConfigDefault.Key.reqDataRequest).append("=").append(generateURLForTestDataRequest());
    }

    private String generateURLForTestDataRequest() {
        JSONObject request = new JSONObject();

        request.put(ConfigDefault.Key.reqDataMode, getModel());
        request.put(ConfigDefault.Key.reqDataMethod, getMethodName());
        request.put(ConfigDefault.Key.reqDataUserData, generateURLForTestDataRequestUserData());

        getTemplate().ifPresent(e -> request.put(ConfigDefault.Key.reqDataTemplate, e));

        try {
            return URLEncoder.encode(request.toString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The request could not be generated.");
        }
    }

    private String generateURLForTestDataRequestUserData() {
        JSONObject requestUserData = new JSONObject();

        requestUserData.put(ConfigDefault.Key.parDataSource, getGeneratorType());
        requestUserData.put(ConfigDefault.Key.parProperties, getGeneratorOptions());

        return requestUserData.toString().replaceAll("\"", "'");
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

    private void generateURLForFeedbackCore(StringBuilder builder) {

        builder.append(getHttpAddress()).append("/").append(ConfigDefault.Key.urlFeedback);
    }

    private void generateURLForFeedbackParameters(StringBuilder builder) { }

    public String generateBodyForFeedback() {
        JSONObject json = new JSONObject();

        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackModel, getModel());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackMethod, getMethodNameQualified());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestSessionId, getTestSessionId());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestSessionLabel, getTestSessionLabel());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackFramework, ConfigDefault.Value.parClient);
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTimestamp, getTimestamp());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackCustom, getCustom());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestSuites, getTestSuites());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackConstraints, getConstraints());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackChoices, getChoices());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestResults, getTestResults());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackGeneratorType, getGeneratorType());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackGeneratorOptions, getGeneratorOptions().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ")));

        return json.toString();
    }

    private void parseFeedbackElement(JSONObject json, String key, Object value) {

        if (value == null) {
            return;
        }

        if (value instanceof String) {
            if (value.toString().equalsIgnoreCase("") || value.toString().equalsIgnoreCase(ConfigDefault.Value.parAll)) {
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

    private String getGeneratorType() {

        return generatorType;
    }

    private String getMethodName() {

        return method;
    }

    private String getModel() {

        return model;
    }

    private JSONObject getTestResults() {

        return testResults;
    }

    private Optional<TypeExport> getTemplate() {

        if (this.template == null || this.template.equals(TypeExport.Raw)) {
            return Optional.empty();
        }

        return Optional.of(template);
    }

    public void setTemplate(TypeExport template) {

        this.template = template;
    }

    private String getMethodNameQualified() {

        return methodNameQualified;
    }

    public void setMethodNameQualified(String methodNameQualified) {

        this.methodNameQualified = methodNameQualified;
    }

    private Map<String, Object> getGeneratorOptions() {

        return generatorOptions;
    }

    public void setGeneratorOptions(Map<String, Object> properties) {
        this.generatorOptions = new HashMap<>();

        properties.forEach((key, value) -> {
            if (key.equalsIgnoreCase(ConfigDefault.Key.parConstraints)) {
                setConstraints(value);
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parTestSuites)) {
                setTestSuites(value);
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parChoices)) {
                setChoices(value);
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parTestSessionLabel)) {
                setTestSessionLabel(value.toString());
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parCustom)) {
                setCustom((Map<String, String>) value);
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parFeedback)) {
                if (value.equals(true) || value.toString().equalsIgnoreCase("true")) {
                    feedbackSetEnable();
                }
            } else {
                this.generatorOptions.put(key, value);
            }
        });

    }

    private String getTestSessionId() {

        return this.testSessionId;
    }

    public void setTestSessionId(String testSessionId) {

        this.testSessionId = testSessionId;
    }

    private String getTestSessionLabel() {

        return this.testSessionLabel;
    }

    private void setTestSessionLabel(String testSessionLabel) {

        this.testSessionLabel = testSessionLabel;
    }

    private int getTimestamp() {

        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {

        this.timestamp = timestamp;
    }

    private Map<String, String> getCustom() {

        return custom;
    }

    private void setCustom(Map<String, String> custom) {

        this.custom = custom;
    }

    private Object getConstraints() {

        return constraints;
    }

    private void setConstraints(Object constraints) {

        this.constraints = constraints;
    }

    private Object getTestSuites() {

        return testSuites;
    }

    private void setTestSuites(Object testSuites) {

        this.testSuites = testSuites;
    }

    private Object getChoices() {

        return choices;
    }

    private void setChoices(Object choices) {

        this.choices = choices;
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

    public HttpClient getHttpClient() {

        return connection.getHttpClient();
    }

    private void feedbackSetEnable() {

        this.enabled = true;
    }

    public void feedbackSetComplete() {

        this.completed = true;

        if (testCasesParsed == testCasesTotal) {
            sendFeedback();
        }
    }

    public Optional<FeedbackHandle> feedbackHandleCreate(String data) {

        if (!this.enabled) {
            return Optional.empty();
        }

        return Optional.of(FeedbackHandle.create(this, data, "0:" + testCasesTotal++));
    }

    public void feedbackHandleRegister(String id, JSONObject feedback) {

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

        HelperConnection.getChunkStreamForFeedback(this);
    }

}
