package com.ecfeed.data;

import com.ecfeed.Config;
import com.ecfeed.TypeExport;
import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionData {

    private final Feedback feedback = Feedback.create(this);

    private final Connection connection;
    private final String generatorType;
    private final String method;
    private final String model;

    private Map<String, Object> generatorOptions = new HashMap<>();
    private Map<String, String> custom = new HashMap<>();
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
                this.constraints = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parTestSuites)) {
                this.testSuites = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parChoices)) {
                this.choices = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parFeedback)) {
                if (e.getValue().toString().equalsIgnoreCase("true")) {
                    this.feedback.enable();
                }
            } else {
                this.generatorOptions.put(e.getKey(), e.getValue());
            }
        });

    }

    public void setTestSessionId(String testSessionId) {

        this.testSessionId = testSessionId;
    }

    public void setTimestamp(int timestamp) {

        this.timestamp = timestamp;
    }

    public void setTemplate(TypeExport template) {

        this.template = template;
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




}
