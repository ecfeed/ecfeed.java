package com.ecfeed.test;

import com.ecfeed.runner.EcFeedFactory;

public class Main {
    public static void main(String[] args) {
        EcFeedFactory.getTestProvider("dupa").validateConnection();

    }
}
