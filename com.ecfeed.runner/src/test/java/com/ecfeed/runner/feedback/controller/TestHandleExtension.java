package com.ecfeed.runner.feedback.controller;

import com.ecfeed.TestHandle;
import org.junit.jupiter.api.extension.*;

public class TestHandleExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver, TestWatcher {

    @Override
    public void beforeTestExecution(ExtensionContext context) {

        TestHandleHelper.getStore(context).put(TestHandleConfig.parDurationStart, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {

        TestHandleHelper.getStore(context).put(TestHandleConfig.parDurationEnd, System.currentTimeMillis());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ExtensionContext.Store store = TestHandleHelper.getStore(context);

        TestHandle testHandle = (TestHandle) store.get(TestHandleConfig.parTestHandle);
        long durationStart = (long) store.get(TestHandleConfig.parDurationStart);
        long durationEnd = (long)store.get(TestHandleConfig.parDurationEnd);
        String message = cause.getMessage();

        testHandle.addFeedback(false, (int) (durationEnd  - durationStart), message);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        ExtensionContext.Store store = TestHandleHelper.getStore(context);

        TestHandle testHandle = (TestHandle) store.get(TestHandleConfig.parTestHandle);
        long durationStart = (long) store.get(TestHandleConfig.parDurationStart);
        long durationEnd = (long) store.get(TestHandleConfig.parDurationEnd);

        testHandle.addFeedback(true, (int) (durationEnd  - durationStart));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        return parameterContext.getParameter().getType().equals(TestHandleBox.class);
    }

    @Override
    public TestHandleBox resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        return TestHandleBox.create(extensionContext);
    }
}
