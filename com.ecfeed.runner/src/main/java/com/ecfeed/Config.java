package com.ecfeed;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Config {

    final static String homeUser;
    final static String homeJava;

    static {
        homeUser = System.getProperty("user.home");
        homeJava = System.getProperty("java.home");
    }

    private Config() { }

    final public static class Value {

        private Value() { }

        final public static String generatorAddress = "https://develop-gen.ecfeed.com";
        final public static String keyStorePassword = "changeit";
        final public static String[] keyStorePath = {
                ".ecfeed/security.p12", "ecfeed/security.p12",
                homeUser + "/.ecfeed/security.p12", homeUser + "/ecfeed/security.p12",
                homeJava + "/lib/security/cacerts"
        };

        final public static String parFeedback = "false";

        final public static String parN = "2";
        final public static String parCoverage = "100";
        final public static String parLength = "10";
        final public static String parDuplicates = "false";
        final public static String parAdaptive = "true";

        final public static String parGenNWise = "genNWise";
        final public static String parGenCartesian = "genCartesian";
        final public static String parGenRandom = "genRandom";
        final public static String parGenStatic = "static";

        final public static String parRequestTypeExport = "requestExport";
        final public static String parRequestTypeStream = "requestData";

        final public static String parClient = "Java";

        final public static String parAll = "ALL";
    }

    final public static class Key {

        private Key() { }

        final public static String parN = "n";
        final public static String parCoverage = "coverage";
        final public static String parLength = "length";
        final public static String parDuplicates = "duplicates";
        final public static String parAdaptive = "adaptive";
        final public static String parConstraints = "constraints";
        final public static String parChoices = "choices";
        final public static String parTestSuites = "testSuites";
        final public static String parFeedback = "feedback";
        final public static String parCustom = "custom";
        final public static String parTestSessionLabel = "testSessionLabel";

        final public static String parDataSource = "dataSource";
        final public static String parProperties = "properties";

        final public static String reqDataMode = "model";
        final public static String reqDataMethod = "method";
        final public static String reqDataUserData = "userData";
        final public static String reqDataRequestType = "requestType";
        final public static String reqDataClient = "client";
        final public static String reqDataRequest = "request";
        final public static String reqDataTemplate = "template";

        final public static String reqFeedbackModel = "modelId";
        final public static String reqFeedbackMethod = "methodInfo";
        final public static String reqFeedbackTestSessionId = "testSessionId'";
        final public static String reqFeedbackTestSessionLabel = "testSessionLabel";
        final public static String reqFeedbackFramework = "framework";
        final public static String reqFeedbackTimestamp = "timestamp";
        final public static String reqFeedbackCustom = "custom";
        final public static String reqFeedbackTestSuites = "testSuites";
        final public static String reqFeedbackConstraints = "constraints";
        final public static String reqFeedbackChoices = "choices";
        final public static String reqFeedbackTestResults = "testResults";
        final public static String reqFeedbackGeneratorType = "generatorType";
        final public static String reqFeedbackGeneratorOptions = "generatorOptions";

        final public static String urlService = "testCaseService";
        final public static String urlHealthCheck = "genServiceVersion";
        final public static String urlFeedback = "streamFeedback";

        final public static String certClient = "connection";
        final public static String certServer = "ca";

        final public static List<String> userAllowedKeys = Arrays.asList(
                parN, parCoverage, parLength, parDuplicates, parAdaptive, parConstraints, parChoices, parTestSuites, parFeedback, parCustom, parTestSessionLabel
        );
    }

    static void validateUserParameters(Map<String, Object> config) {
        List<String> incorrectKeys = config.keySet().stream()
                .filter( k -> !Key.userAllowedKeys.contains(k)).collect(Collectors.toList());

        if (incorrectKeys.size() > 0) {
            throw new IllegalArgumentException("The following configuration parameters are invalid: " +
                    Arrays.toString(incorrectKeys.toArray()));
        }
    }

}
