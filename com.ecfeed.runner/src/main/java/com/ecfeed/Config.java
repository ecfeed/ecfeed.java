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

    final static class Value {

        private Value() { }

        final static String generatorAddress = "https://gen.ecfeed.com";
        final static String keyStorePassword = "changeit";
        final static String[] keyStorePath = {
                ".ecfeed/security.p12", "ecfeed/security.p12",
                homeUser + "/.ecfeed/security.p12", homeUser + "/ecfeed/security.p12",
                homeJava + "/lib/security/cacerts"
        };

        final static String parN = "2";
        final static String parCoverage = "100";
        final static String parLength = "10";
        final static String parDuplicates = "false";
        final static String parAdaptive = "true";

        final static String parGenNWise = "genNWise";
        final static String parGenCartesian = "genCartesian";
        final static String parGenRandom = "genRandom";
        final static String parGenStatic = "static";

        final static String parRequestTypeExport = "requestExport";
        final static String parRequestTypeStream = "requestData";

        final static String parClient = "java";
    }

    final static class Key {

        private Key() { }

        final static String parN = "n";
        final static String parCoverage = "coverage";
        final static String parLength = "length";
        final static String parDuplicates = "duplicates";
        final static String parAdaptive = "adaptive";
        final static String parConstraints = "constraints";
        final static String parChoices = "choices";
        final static String parTestSuites = "testSuites";

        final static String parDataSource = "dataSource";
        final static String parProperties = "properties";

        final static String parModel = "model";
        final static String parMethod = "method";
        final static String parUserData = "userData";
        final static String parRequestType = "requestType";
        final static String parClient = "client";
        final static String parRequest = "request";
        final static String parTemplate = "template";

        final static String urlService = "testCaseService";
        final static String urlHealthCheck = "genServiceVersion";

        final static String certClient = "connection";
        final static String certServer = "ca";

        final static List<String> userAllowedKeys = Arrays.asList(
                parN, parCoverage, parLength, parDuplicates, parAdaptive, parConstraints, parChoices, parTestSuites
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
