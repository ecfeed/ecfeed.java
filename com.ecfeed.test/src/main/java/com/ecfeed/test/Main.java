package com.ecfeed.test;

import com.ecfeed.runner.EcFeedFactory;
import com.ecfeed.runner.design.TestProvider;

public class Main {
    public static void main(String[] args) {
        TestProvider provider = EcFeedFactory.getTestProvider("dupa");

    }
}
