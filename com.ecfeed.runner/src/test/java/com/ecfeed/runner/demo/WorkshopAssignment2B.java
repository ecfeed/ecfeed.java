package com.ecfeed.runner.demo;

import com.ecfeed.TestHandle;
import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsNWise;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class WorkshopAssignment2B {

    static String model = "IMHL-K0DU-2U0I-J532-25J9";
    static String method = "QuickStart.test";

    static Iterable<Object[]> genNWise() {
        return TestProvider.create(model).generateNWise(method, ParamsNWise.create().feedback());
    }

    @ParameterizedTest
    @MethodSource("genNWise")
    void genNWise(int arg1, int arg2, int arg3, TestHandle handle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
        handle.addFeedback(true);
    }
}
