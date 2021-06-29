package com.ecfeed.runner.feedback.controller;

import org.junit.jupiter.api.extension.ExtensionContext;

public class TestHandleHelper {

    static ExtensionContext.Store getStore(ExtensionContext context) {

        return context.getStore(ExtensionContext.Namespace.create(TestHandleConfig.storeReference, context.getRequiredTestMethod()));
    }

}
