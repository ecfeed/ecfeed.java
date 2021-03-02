package com.ecfeed;

import java.util.ArrayList;
import java.util.List;

public class Feedback {

    private final List<FeedbackItem> results = new ArrayList<>();

    private String testSessionId;
    private String modelId;
    private String methodInfo;
    private String framework;
    private String timestamp;
    private String generatorType;
    private String generatorOptions;
    private String testSessionLabel;
    private String constraints;
    private String choices;
    private String custom;
    private String testSuites;

    public List<FeedbackItem> getResults() {
        return results;
    }

    void addResult(FeedbackItem result) {
        results.add(result);
    }

    public String getTestSessionId() {
        return testSessionId;
    }

    public void setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(String methodInfo) {
        this.methodInfo = methodInfo;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(String generatorType) {
        this.generatorType = generatorType;
    }

    public String getGeneratorOptions() {
        return generatorOptions;
    }

    public void setGeneratorOptions(String generatorOptions) {
        this.generatorOptions = generatorOptions;
    }

    public String getTestSessionLabel() {
        return testSessionLabel;
    }

    public void setTestSessionLabel(String testSessionLabel) {
        this.testSessionLabel = testSessionLabel;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(String testSuites) {
        this.testSuites = testSuites;
    }





}
