package com.ecfeed.test;

import com.ecfeed.TestProvider;
import com.ecfeed.TypeExport;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        exportNwiseFromExampleModelWithFeedbackStub();
    }

    private static void exportNwiseFromExampleModelWithFeedbackStub() {
        // on test database
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
