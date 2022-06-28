package com.ecfeed.runner.feedback.controller;

import com.ecfeed.TestHandle;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestHandleBox {
    private final ExtensionContext extensionContext;

    private TestHandleBox(ExtensionContext extensionContext) {

        this.extensionContext = extensionContext;
    }

    static TestHandleBox create(ExtensionContext context) {

        return new TestHandleBox(context);
    }

    public void registerTestHandle(TestHandle testHandle) {

        if (TestHandleHelper.getStore(extensionContext).get(TestHandleConfig.parTestHandle) != null) {
            return;
        }

        TestHandleHelper.getStore(extensionContext).put(TestHandleConfig.parTestHandle, testHandle);
    }

}
