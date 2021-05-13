package com.ecfeed.data;

import com.ecfeed.Config;
import com.ecfeed.TypeExport;
import org.apache.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionData {

    private final static String FRAMEWORK = "Java";

    private final ConnectionData connectionData;

    private boolean feedbackCollect = false;
    private boolean feedbackFinished = false;
    private int testCasesTotal = 0;
    private int testCasesParsed = 0;

    private String testSessionId;
    private int timestamp;

    private String generatorType;
    private Map<String, Object> generatorOptions = new HashMap<>();

    private String methodName;
    private String methodNameQualified;
    private String model;

    private Optional<Map<String, String>> custom;
    private Optional<TypeExport> template = Optional.empty();
    private Optional<Object> constraints = Optional.empty();
    private Optional<Object> testSuites = Optional.empty();
    private Optional<Object> choices = Optional.empty();
    private Optional<String> testSessionLabel = Optional.empty();

//-------------------------------------------------------------------------------------

    private SessionData(ConnectionData connectionData, String model) {
        this.connectionData = connectionData;
        this.model = model;
    }

    public static SessionData create(ConnectionData connectionData, String model) {
        return new SessionData(connectionData, model);
    }

//-------------------------------------------------------------------------------------

    public SessionData updateRequestData(String method, String generator) {
        this.methodName = method;
        this.generatorType = generator;

        return this;
    }

    public SessionData updateRequestTemplate(TypeExport template) {
        this.template = Optional.ofNullable(template);

        return this;
    }

    public SessionData updateRequestProperties(Map<String, Object> properties) {
        this.generatorOptions = new HashMap<>();

        properties.entrySet().stream().forEach(e -> {
            if (e.getKey().equalsIgnoreCase(Config.Key.parConstraints)) {
                this.constraints = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parTestSuites)) {
                this.testSuites = Optional.of(e.getValue());
            } else if (e.getKey().equalsIgnoreCase(Config.Key.parChoices)) {
                this.choices = Optional.of(e.getValue());
            } else {
                this.generatorOptions.put(e.getKey(), e.getValue());
            }
        });

        return this;
    }

//-------------------------------------------------------------------------------------

    public String getGeneratorAddress() {
        return connectionData.getHttpAddress();
    }

    public String getMethodName() {
        return methodName;
    }

    public String getModel() {
        return model;
    }

    public String getGeneratorType() {
        return generatorType;
    }

    public Map<String, Object> getGeneratorOptions() {
        return generatorOptions;
    }

    public Optional<TypeExport> getTemplate() {
        return template;
    }

    public HttpClient getHttpClient() {
        return connectionData.getHttpClient();
    }
}
