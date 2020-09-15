package com.ecfeed.runner;

import com.ecfeed.runner.design.TestProvider;
import com.ecfeed.runner.implementation.TestProviderDefault;

import java.util.Map;

public final class EcFeedFactory {

    private EcFeedFactory() { }

    public static TestProvider getTestProvider(String model) {

        return TestProviderDefault.getTestProvider(model);
    }

    public static TestProvider getTestProvider(String model, Map<String, String> config) {

        return TestProviderDefault.getTestProvider(model, config);
    }
}
