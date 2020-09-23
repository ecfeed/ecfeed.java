package com.ecfeed;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Configuration {

    private final static String homeUser;
    private final static String homeJava;

    static {
        homeUser = System.getProperty("user.home");
        homeJava = System.getProperty("java.home");
    }

    private Configuration() { }

    public final static class Value {

        private Value() { }

        public final static String generatorAddress = "https://develop-gen.ecfeed.com";
        public final static String keyStorePassword = "changeit";
        public final static String[] keyStorePath = {
                ".ecfeed/security.p12", "ecfeed/security.p12",
                homeUser + "/.ecfeed/security.p12", homeUser + "/ecfeed/security.p12",
                homeJava + "/lib/security/cacerts"
        };

        public final static String parN = "2";
        public final static String parCoverage = "100";
        public final static String parLength = "10";
        public final static String parDuplicates = "false";
        public final static String parAdaptive = "true";

        public final static String parGenNWise = "genNWise";
        public final static String parGenCartesian = "genCartesian";
        public final static String parGenRandom = "genRandom";
        public final static String parGenStatic = "static";

        public final static String parRequestTypeExport = "requestExport";
        public final static String parRequestTypeStream = "requestData";

        public final static String parClient = "java";
    }

    public final static class Key {

        private Key() { }

        public final static String parN = "n";
        public final static String parCoverage = "coverage";
        public final static String parLength = "length";
        public final static String parDuplicates = "duplicates";
        public final static String parAdaptive = "adaptive";
        public final static String parConstraints = "constraints";
        public final static String parChoices = "choices";
        public final static String parTestSuites = "testSuites";

        public final static String parDataSource = "dataSource";
        public final static String parProperties = "properties";

        public final static String parModel = "model";
        public final static String parMethod = "method";
        public final static String parUserData = "userData";
        public final static String parRequestType = "requestType";
        public final static String parClient = "client";
        public final static String parRequest = "request";
        public final static String parTemplate = "template";

        public final static String urlService = "testCaseService";
        public final static String urlHealthCheck = "genServiceVersion";

        public final static String certClient = "connection";
        public final static String certServer = "ca";

        public final static List<String> userKeys = Arrays.asList(
                parN, parCoverage, parLength, parDuplicates, parAdaptive, parConstraints, parChoices, parTestSuites
        );
    }

    public static void validateUserParameters(Map<String, Object> config) {
        List<String> incorrectKeys = config.keySet().stream()
                .filter( k -> !Key.userKeys.contains(k)).collect(Collectors.toList());

        if (incorrectKeys.size() > 0) {
            throw new IllegalArgumentException("The following configuration parameters are invalid: " +
                    Arrays.toString(incorrectKeys.toArray()));
        }
    }

}
