package com.ecfeed;

public class FeedbackSession {

    private String sessionId;
    private String modelId;
    private String methodInfo;
    private String framework;
    private long timestamp;
    private String generatorType;
    private String generatorOptions;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public  void setModelId(String modelId) {
        this.modelId  =  modelId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setMethodInfo(String methodInfo) {
        this.methodInfo = methodInfo;
    }

    public String getMethodInfo() {
        return methodInfo;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getFramework() {
        return framework;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setGeneratorType(String generatorType) {
        this.generatorType = generatorType;
    }

    public String getGeneratorType() {
        return generatorType;
    }
    public void setGeneratorOptions(String generatorOptions) {
        this.generatorOptions = generatorOptions;
    }

    public String getGeneratorOptions() {
        return generatorOptions;
    }
}
