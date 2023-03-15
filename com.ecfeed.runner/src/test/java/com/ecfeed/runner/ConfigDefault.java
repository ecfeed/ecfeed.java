package com.ecfeed.runner;

import com.ecfeed.TestProvider;

import java.util.HashMap;
import java.util.Map;

public class ConfigDefault {

    public static final boolean PROD = true;
    public static final boolean DEVELOP = false;

    public static String KEYSTORE_PROD = "C:\\Users\\kskor\\.ecfeed\\security.p12";
    public static final String MODEL_DEVELOP = "QERK-K7BW-ME4G-W3TT-NT32";
    public static final String MODEL_PROD = "IMHL-K0DU-2U0I-J532-25J9";
    public static final String MODEL_DUMMY = "XXXX-XXXX-XXXX-XXXX-XXXX";

    public static TestProvider getTestProvider(boolean prod) {

        return prod ? getTestProviderProd() : getTestProviderDevelop();
    }

    private static TestProvider getTestProviderProd() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePath", KEYSTORE_PROD);

        return TestProvider.create(ConfigDefault.MODEL_PROD, configProvider);
    }

    private static TestProvider getTestProviderDevelop() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", "https://develop-gen.ecfeed.com");

        return TestProvider.create(ConfigDefault.MODEL_DEVELOP, configProvider);
    }

    public static final String F_TEST = "QuickStart.test";
    public static final String F_10x10 = "com.example.test.Playground.size_10x10";
    public static final String F_100x2 = "com.example.test.Playground.size_100x2";
    public static final String F_LOAN_2 = "com.example.test.LoanDecisionTest2.generateCustomerData";
    public static final String F_STRUCTURE = "TestStructure.generate";

}
