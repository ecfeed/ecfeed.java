package com.ecfeed.test;

import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsCartesian;
import com.ecfeed.type.TypeExport;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TestProvider testProvider = TestProvider.create("LRXC-015K-GJB0-2A9F-CGA2");

        for (String chunk : testProvider.exportNWise("QuickStart.test", TypeExport.CSV, new HashMap<>())) {
            System.out.println(chunk);
        }
    }
}
