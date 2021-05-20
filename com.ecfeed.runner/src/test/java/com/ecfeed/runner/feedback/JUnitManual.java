package com.ecfeed.runner.feedback;

import com.ecfeed.TestHandle;
import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsNWise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JUnitManual {

    private static final String model = "LRXC-015K-GJB0-2A9F-CGA2";
    private static final String method = "com.example.test.Playground.size_10x10";

    static Iterable<Object[]> method() {
        return TestProvider.create(model).generateNWise(method, ParamsNWise.create().feedback());
    }

    private void validate(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        Assertions.assertNotEquals("a0", a, () -> testHandle.addFeedback(false, "Failed - a"));
        Assertions.assertNotEquals("b1", b, () -> testHandle.addFeedback(false, "Failed - b"));
        Assertions.assertNotEquals("h6", h, () -> testHandle.addFeedback(false, "Failed - h"));

        testHandle.addFeedback(true, "OK");
    }

    @ParameterizedTest
    @MethodSource("method")
    void defaultNWise(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        validate(a, b, c, d, e, f, g, h, i, j, testHandle);
    }
}
