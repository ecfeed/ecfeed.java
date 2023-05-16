package com.ecfeed.runner;

import com.ecfeed.TestProvider;

import java.util.HashMap;
import java.util.Map;

public class ConfigDefault {

    public enum Stage { PROD, DEVELOP, LOCAL_BASIC, LOCAL_TEAM};

    public static String KEYSTORE_PROD = "C:\\Users\\kskor\\.ecfeed\\security.p12";
    public static String KEYSTORE_DEVELOP = "C:\\Users\\kskor\\.ecfeed\\security_dev.p12";
    public static String KEYSTORE_LOCAL = "C:\\Users\\kskor\\.ecfeed\\security_dev.p12";
    public static final String MODEL_PROD = "IMHL-K0DU-2U0I-J532-25J9";
    public static final String MODEL_DEVELOP = "QERK-K7BW-ME4G-W3TT-NT32";
    public static final String MODEL_DUMMY = "XXXX-XXXX-XXXX-XXXX-XXXX";
    public static final String MODEL_LOCAL_BASIC = "TestUuid11:basic";
    public static final String MODEL_LOCAL_TEAM = "TestUuid11:team";
    public static final String GENERATOR_ADDRESS_PROD = "https://gen.ecfeed.com";
    public static final String GENERATOR_ADDRESS_DEVELOP = "https://develop-gen.ecfeed.com";
    public static final String GENERATOR_ADDRESS_LOCAL = "https://localhost:8090";
    public static final String KEYSTORE_PASSWORD = "changeit";

    public static final String F_TEST = "QuickStart.test";
    public static final String F_10x10 = "com.example.test.Playground.size_10x10";
    public static final String F_100x2 = "com.example.test.Playground.size_100x2";
    public static final String F_LOAN_2 = "com.example.test.LoanDecisionTest2.generateCustomerData";
    public static final String F_STRUCTURE = "TestStructure.generate";

    public static TestProvider getTestProviderRemote(Stage stage) {

        switch (stage) {
            case PROD: return getTestProviderRemoteProd();
            case DEVELOP: return getTestProviderRemoteDevelop();
            case LOCAL_BASIC: return getTestProviderLocalBasic();
            case LOCAL_TEAM: return getTestProviderLocalTeam();
        }

        throw new IllegalArgumentException("The stage definition is erroneous!");
    }

    private static TestProvider getTestProviderRemoteProd() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePath", KEYSTORE_PROD);

        return TestProvider.create(ConfigDefault.MODEL_PROD, configProvider);
    }

    private static TestProvider getTestProviderRemoteDevelop() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", GENERATOR_ADDRESS_DEVELOP);
        configProvider.put("keyStorePath", KEYSTORE_DEVELOP);

        return TestProvider.create(ConfigDefault.MODEL_DEVELOP, configProvider);
    }

    private static TestProvider getTestProviderLocalBasic() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", GENERATOR_ADDRESS_LOCAL);
        configProvider.put("keyStorePath", KEYSTORE_LOCAL);

        return TestProvider.create(ConfigDefault.MODEL_LOCAL_BASIC, configProvider);
    }

    private static TestProvider getTestProviderLocalTeam() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", GENERATOR_ADDRESS_LOCAL);
        configProvider.put("keyStorePath", KEYSTORE_LOCAL);

        return TestProvider.create(ConfigDefault.MODEL_LOCAL_TEAM, configProvider);
    }

    public static String getGeneratorAddress(Stage stage) {

        switch (stage) {
            case PROD: return GENERATOR_ADDRESS_PROD;
            case DEVELOP: return GENERATOR_ADDRESS_DEVELOP;
            case LOCAL_BASIC:
            case LOCAL_TEAM:
                return GENERATOR_ADDRESS_LOCAL;
        }

        return null;
    }

    public static String getKeystorePath(Stage stage) {

        switch (stage) {
            case PROD: return KEYSTORE_PROD;
            case DEVELOP: return KEYSTORE_DEVELOP;
            case LOCAL_BASIC:
            case LOCAL_TEAM:
                return KEYSTORE_LOCAL;
        }

        return null;
    }

    public static String getModel(Stage stage) {

        switch (stage) {
            case PROD: return MODEL_PROD;
            case DEVELOP: return MODEL_DEVELOP;
            case LOCAL_BASIC: return MODEL_LOCAL_BASIC;
            case LOCAL_TEAM: return MODEL_LOCAL_TEAM;
        }

        return null;
    }
}
