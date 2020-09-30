package com.ecfeed.test;

import com.ecfeed.junit.TestProvider;
import com.ecfeed.junit.TypeExport;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TestProvider testProvider = TestProvider.create("ZCPH-DFYI-R7R7-R6MM-89L8");

        for (String chunk : testProvider.exportNWise("QuickStart.test", TypeExport.CSV, new HashMap<>())) {
            System.out.println(chunk);
        }
    }
}
