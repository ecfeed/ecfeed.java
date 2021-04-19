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

        boolean isFirst = true;
        int dataIndex = 0;
        Iterator<String> it = testDataChunks.iterator();

        while (it.hasNext()) {

            String testDataChunk = it.next();
            System.out.println(testDataChunk);

            if (isFirst) {
                isFirst = false;
                continue; // skip header
            }

            String id = createTestResultId(dataIndex);
            String data = createTestResultData(dataIndex);
            dataIndex++;

            testProvider.setFeedbackResult( id, data, true, 100, it.hasNext());
        }

        System.out.println("End");
    }

    private static String createTestResultId(int dataIndex) {

        String[] arr =
                {
                    "0:0",
                    "0:1",
                    "0:2",
                    "0:3"
                };

        String result = arr[dataIndex];

        return result;
    }

    private static String createTestResultData(int dataIndex) {

        String[] arr =
                {
                    "{#testCase#:[{#name#:#choice11#,#value#:#V11#},{#name#:#choice21#,#value#:#V21#}]}",
                    "{#testCase#:[{#name#:#choice12#,#value#:#V12#},{#name#:#choice21#,#value#:#V21#}]}",
                    "{#testCase#:[{#name#:#choice12#,#value#:#V12#},{#name#:#choice22#,#value#:#V22#}]}",
                    "{#testCase#:[{#name#:#choice11#,#value#:#V11#},{#name#:#choice22#,#value#:#V22#}]}"

                };

        String result = arr[dataIndex];

        result = result.replace("#", "\"");

        return result;
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
