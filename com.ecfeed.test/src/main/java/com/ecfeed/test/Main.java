package com.ecfeed.test;

import com.ecfeed.FeedbackSession;
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

        TestProvider testProvider = TestProvider.create("TestUuid11");

        FeedbackSession feedbackSession =  new FeedbackSession();
        testProvider.initializeFeedback(feedbackSession);

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
