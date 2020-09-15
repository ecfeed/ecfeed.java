package com.ecfeed.runner;

public final class Config {

    private final static String homeUser;
    private final static String homeJava;

    static {
        homeUser = System.getProperty("user.home");
        homeJava = System.getProperty("java.home");
    }

    private Config() { }

    public final static class Value {

        private Value() { }

        public static String generatorAddress = "https://develop-gen.ecfeed.com";
        public static String keyStorePassword = "changeit";
        public static String[] keyStorePath = {
                ".ecfeed/security.p12", "ecfeed/security.p12",
                homeUser + "/.ecfeed/security.p12", homeUser + "/ecfeed/security.p12",
                homeJava + "/lib/security/cacerts"
        };

        public static String parN = "2";
        public static String parCoverage = "100";

        public static String parGenNWise = "genNWise";

        public static String parRequestTypeExport = "requestExport";
        public static String parRequestTypeStream = "requestData";

        public static String parClient = "java";
    }

    public final static class Name {

        private Name() { }

        public static String parN = "n";
        public static String parCoverage = "coverage";

        public static String parDataSource = "dataSource";
        public static String parProperties = "properties";

        public static String parModel = "model";
        public static String parMethod = "method";
        public static String parUserData = "userData";
        public static String parRequestType = "requestType";
        public static String parClient = "client";
        public static String parRequest = "request";
        public static String parTemplate = "template";

        public static String urlService = "testCaseService";

        public static String certClient = "connection";
        public static String certServer = "ca";
    }

}
