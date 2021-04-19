package com.ecfeed.test;

import com.ecfeed.FeedbackData;
import com.ecfeed.TestProvider;
import com.ecfeed.TypeExport;

import java.util.HashMap;
import java.util.Iterator;

public class Main {

    // Remark. On test database.

    public static void main(String[] args) {
        exportFeedbackIndividualTestResults();
    }

    private static void exportFeedbackIndividualTestResults() {

        FeedbackData feedbackData = createFeedbackData();

        TestProvider testProvider = TestProvider.create("TestUuid11");
        testProvider.initializeFeedback(feedbackData);

        Iterable<String> testDataChunks =
                testProvider.exportNWise(
                        "test.Class1.testMethod(String arg1, String arg2)",
                        TypeExport.CSV,
                        new HashMap<>());

        Iterator<String> it = testDataChunks.iterator();

        while (it.hasNext()) {
            String testDataChunk = it.next();
            System.out.println(testDataChunk);

            testProvider.setFeedbackResult(true, 100, it.hasNext());
        }

        System.out.println("End");
    }

    private static FeedbackData createFeedbackData() {

        FeedbackData feedbackData =  new FeedbackData();

        long currentMilliseconds = System.currentTimeMillis();

        final String testSessionId = "testSession" + currentMilliseconds;
        feedbackData.setTestSessionId(testSessionId);

        feedbackData.setFramework("Java");
        feedbackData.setTimestamp(currentMilliseconds);
        feedbackData.setGeneratorType("NWise");
        feedbackData.setGeneratorOptions("n=2, coverage=100");

        return feedbackData;
    }

    private static void exportNwiseFromExampleModelWithFeedbackStub() {

        TestProvider testProvider = TestProvider.create("TestUuid11");

        for (String chunk : testProvider.exportNWise("test.Class1.testMethod(String arg1, String arg2)", TypeExport.CSV, new HashMap<>())) {
            System.out.println(chunk);
        }

        testProvider.sendFixedFeedback();
    }

    private static void exportNwiseFromExampleModel() {
        // on test database
        TestProvider testProvider = TestProvider.create("TestUuid11");

        for (String chunk : testProvider.exportNWise("test.Class1.testMethod(String arg1, String arg2)", TypeExport.CSV, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    public void exportNwiseFromWelcomeModel() {

        TestProvider testProvider = TestProvider.create("ZCPH-DFYI-R7R7-R6MM-89L8");

        for (String chunk : testProvider.exportNWise("QuickStart.test", TypeExport.CSV, new HashMap<>())) {
            System.out.println(chunk);
        }
    }
}
