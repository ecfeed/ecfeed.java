package com.ecfeed.runner.demo;

import com.ecfeed.TestHandle;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Oracle {

    static void validateF10x10(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        Assertions.assertNotEquals("a0", a, () -> testHandle.addFeedback(false, "Failed - a"));
        Assertions.assertNotEquals("b1", b, () -> testHandle.addFeedback(false, "Failed - b"));
        Assertions.assertNotEquals("h6", h, () -> testHandle.addFeedback(false, "Failed - h"));

        testHandle.addFeedback(true, "OK");
    }

    static void validateFeedbackF10x10(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        Assertions.assertNotEquals("a0", a, () -> testHandle.addFeedback(false, getDuration(), "Failed - a", getCustom()));
        Assertions.assertNotEquals("b1", b, () -> testHandle.addFeedback(false, getDuration(), "Failed - b", getCustom()));
        Assertions.assertNotEquals("h6", h, () -> testHandle.addFeedback(false, getDuration(), "Failed - h", getCustom()));

        testHandle.addFeedback(true, getDuration(), "OK", getCustom());
    }

    static void validateF100x2(String a, String b, TestHandle testHandle) {
        Assertions.assertNotEquals("a00", a, () -> testHandle.addFeedback(false, "Failed - a"));
        Assertions.assertNotEquals("b00", b, () -> testHandle.addFeedback(false, "Failed - b"));

        testHandle.addFeedback(true, "OK");
    }

    static void validateFeedbackF100x2(String a, String b, TestHandle testHandle) {
        Assertions.assertNotEquals("a00", a, () -> testHandle.addFeedback(false, getDuration(), "Failed - a", getCustom()));
        Assertions.assertNotEquals("b00", b, () -> testHandle.addFeedback(false, getDuration(), "Failed - b", getCustom()));

        testHandle.addFeedback(true, getDuration(), "OK", getCustom());
    }

    static void validateFTest(int arg1, int arg2, int arg3, TestHandle testHandle) {
        Assertions.assertTrue(arg1 < 2, () -> testHandle.addFeedback(false, "Failed - arg1 < 2"));

        testHandle.addFeedback(true, "OK");
    }

    static void validateFeedbackFTest(int arg1, int arg2, int arg3, TestHandle testHandle) {
        Assertions.assertTrue(arg1 < 2, () -> testHandle.addFeedback(false, getDuration(), "Failed - arg1 < 2", getCustom()));

        testHandle.addFeedback(true, getDuration(), "OK", getCustom());
    }

    static void validateMapFTest(int arg1, int arg2, int arg3, TestHandle testHandle) {

        if (arg1 == arg2 && arg2 == arg3) {
            testHandle.addFeedback(false, getDuration(), "Failed - arg1 < 2", getCustom());
        } else {
            testHandle.addFeedback(true, getDuration(), "OK", getCustom());
        }
    }

    private static int getDuration() {

        return ThreadLocalRandom.current().nextInt(100, 200);
    }

    private static Map<String, String> getCustom() {
        Map<String, String> custom = new HashMap<>();

        for (int i = 0; i < ThreadLocalRandom.current().nextInt(2, 10); i++) {
            custom.put("key " + i, "value " + i);
        }

        return custom;
    }

}
