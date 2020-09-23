package com.ecfeed;

import com.ecfeed.design.TestProvider;
import com.ecfeed.implementation.DefaultTestProvider;

import java.util.Map;

public final class EcFeedFactory {

    private EcFeedFactory() { }

    public static TestProvider getTestProvider(String model) {

        return DefaultTestProvider.getTestProvider(model);
    }

    public static TestProvider getTestProvider(String model, Map<String, String> config) {

        return DefaultTestProvider.getTestProvider(model, config);
    }
}