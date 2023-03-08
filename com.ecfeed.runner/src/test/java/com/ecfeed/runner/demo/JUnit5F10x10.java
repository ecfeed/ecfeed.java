package com.ecfeed.runner.demo;

import com.ecfeed.TestHandle;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.params.ParamsRandom;
import com.ecfeed.runner.ConfigDefault;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class JUnit5F10x10 {

    static Iterable<Object[]> genRandomQuantitySingle() {
        return ConfigDefault.getTestProvider(false).generateRandom(ConfigDefault.F_10x10, ParamsRandom.create()
                .feedback()
                .length(1)
                .label("Random / Quantity - Single"));
    }

    static Iterable<Object[]> genRandomQuantityShort() {
        return ConfigDefault.getTestProvider(ConfigDefault.PROD).generateRandom(ConfigDefault.F_10x10, ParamsRandom.create()
                .feedback()
                .length(ThreadLocalRandom.current().nextInt(100, 500))
                .label("Random / Quantity - Short"));
    }

    static Iterable<Object[]> genRandomQuantityLong() {
        return ConfigDefault.getTestProvider(ConfigDefault.PROD).generateRandom(ConfigDefault.F_10x10, ParamsRandom.create()
                .feedback()
                .length(ThreadLocalRandom.current().nextInt(1000, 5000))
                .label("Random / Quantity - Long"));
    }

    static Iterable<Object[]> genRandomCustom() {
        return ConfigDefault.getTestProvider(ConfigDefault.PROD).generateRandom(ConfigDefault.F_10x10, ParamsRandom.create()
                .feedback()
                .length(1)
                .label("Random / Custom")
                .custom(new HashMap<>(){{put("key1", "value1"); put("key2", "value2");}}));
    }

    static Iterable<Object[]> genNWise() {
        return ConfigDefault.getTestProvider(ConfigDefault.PROD).generateNWise(ConfigDefault.F_10x10, ParamsNWise.create()
                .feedback()
                .label("NWise"));
    }

    static Iterable<Object[]> genPairwise() {
        return ConfigDefault.getTestProvider(ConfigDefault.PROD).generateNWise(ConfigDefault.F_10x10, ParamsNWise.create()
                .feedback()
                .label("Pairwise"));
    }

    static Iterable<Object[]> genNWiseTest() {
        return ConfigDefault.getTestProvider(ConfigDefault.PROD).generateNWise(ConfigDefault.F_10x10, ParamsNWise.create()
                .feedback()
                .label("NWise / Feedback"));
    }

    @ParameterizedTest
    @MethodSource("genRandomQuantitySingle")
    void genRandomQuantitySingle(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomQuantityShort")
    void genRandomQuantityShort(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomQuantityLong")
    void randomQuantityLong(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomCustom")
    void randomCustom(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWise")
    void genNWise(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genPairwise")
    void genPairwise(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseTest")
    void genNWiseTest(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + ", e = " + e + ", f = " + f + ", g = " + g + ", h = " + h + ", i = " + i + ", j = " + j);
        Oracle.validateFeedbackF10x10(a, b, c, d, e, f, g, h, i, j, testHandle);
    }

}
