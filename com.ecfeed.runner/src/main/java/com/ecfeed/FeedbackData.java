package com.ecfeed;

import org.json.JSONObject;

public class FeedbackData {

    private String testSessionId;
    private String modelId;
    private String methodInfo;
    private String framework;
    private long timestamp;
    private String generatorType;
    private String generatorOptions;

    // TODO
    // getSessionGeneratorConstraints
    // getSessionGeneratorChoices
    // getTestSuites
    // getCustom


    public void setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
    }

    public String getTestSessionId() {
        return testSessionId;
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

    FeedbackResults feedbackResults;
    
    public void addFeedbackResult(FeedbackResult feedbackResult) {

    }

    public String serialize() {

        JSONObject feedbackObject = new JSONObject();

        feedbackObject.put("testSessionId", this.testSessionId);
        feedbackObject.put("modelId", this.modelId);
        feedbackObject.put("methodInfo", this.methodInfo);
        feedbackObject.put("framework", this.framework);
        feedbackObject.put("timestamp", this.timestamp);
        feedbackObject.put("generatorType", this.generatorType);
        feedbackObject.put("generatorOptions", this.generatorOptions);
        feedbackObject.put("testResults", "#");

        String feedbackString = feedbackObject.toString();

        return feedbackString;

//        "'testSessionId': '" + testSessionId + "', " +
//                "'modelId': 'TestUuid11', " +
//                "'methodInfo': 'test.Class1.testMethod(String arg1, String arg2)', " +
//                "'framework': 'Python', " +
//                "'timestamp': 1618401006, " +
//                "'generatorType': 'NWise', " +
//                "'generatorOptions': 'n=2, coverage=100', " +
//
//                "'testResults': " +
//                "{ " +
//                "'0:0': {'data': '{#testCase#:[{#name#:#choice11#,#value#:#V11#},{#name#:#choice21#,#value#:#V21#}]}', 'status': 'P', 'duration': 1394}, " +
//                "'0:1': {'data': '{#testCase#:[{#name#:#choice12#,#value#:#V12#},{#name#:#choice21#,#value#:#V21#}]}', 'status': 'F', 'duration': 1513}, " +
//                "'0:2': {'data': '{#testCase#:[{#name#:#choice12#,#value#:#V12#},{#name#:#choice22#,#value#:#V22#}]}', 'status': 'F', 'duration': 1513}, " +
//                "'0:3': {'data': '{#testCase#:[{#name#:#choice11#,#value#:#V11#},{#name#:#choice22#,#value#:#V22#}]}', 'status': 'F', 'duration': 1513}" +
//                "} " +
//
//                "}";
    }
    
}
