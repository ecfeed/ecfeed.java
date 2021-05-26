package com.ecfeed.runner.demo;

import com.ecfeed.TestHandle;
import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsCartesian;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.params.ParamsRandom;
import com.ecfeed.params.ParamsStatic;
import com.ecfeed.runner.ConfigDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class JUnit5FTest {

    static Iterable<Object[]> genRandomQuantitySingle() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_TEST, ParamsRandom.create()
                .feedback()
                .length(1)
                .label("Random / Quantity - Single"));
    }

    static Iterable<Object[]> genRandomQuantityShort() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_TEST, ParamsRandom.create()
                .feedback()
                .length(ThreadLocalRandom.current().nextInt(100, 500))
                .label("Random / Quantity - Short"));
    }

    static Iterable<Object[]> genRandomQuantityLong() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_TEST, ParamsRandom.create()
                .feedback()
                .length(ThreadLocalRandom.current().nextInt(1000, 5000))
                .label("Random / Quantity - Long"));
    }

    static Iterable<Object[]> genRandom() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_TEST, ParamsRandom.create()
                .feedback()
                .label("Random"));
    }

    static Iterable<Object[]> genRandomAdaptive() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_TEST, ParamsRandom.create()
                .feedback()
                .length(10)
                .adaptive(false)
                .label("Random - Adaptive"));
    }

    static Iterable<Object[]> genRandomDuplicates() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_TEST, ParamsRandom.create()
                .feedback()
                .length(10)
                .duplicates(true)
                .label("Random - Duplicates"));
    }

    static Iterable<Object[]> genNWise() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .label("NWise"));
    }

    static Iterable<Object[]> genNWiseN() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .n(3)
                .label("NWise - N"));
    }

    static Iterable<Object[]> genNWiseCoverage() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .coverage(50)
                .label("NWise - Coverage"));
    }

    static Iterable<Object[]> genNWiseConstraintsNone() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .constraints("NONE")
                .label("NWise / Constraints - None"));
    }

    static Iterable<Object[]> genNWiseConstraintsSelected() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .constraints(new String[]{"constraint1", "constraint2"})
                .label("NWise / Constraints - Selected"));
    }

    static Iterable<Object[]> genNWiseChoicesSelected() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .choices(new HashMap<>(){{put("arg1", new String[]{"choice1", "choice2"});put("arg2", new String[]{"choice2", "choice3"});}})
                .label("NWise / Choices - Selected"));
    }

    static Iterable<Object[]> genNWiseCustom() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .custom(new HashMap<>(){{put("key1","value1");put("key2","value2");}})
                .label("NWise / Custom"));
    }

    static Iterable<Object[]> genPairwise() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .label("Pairwise"));
    }

    static Iterable<Object[]> genCartesian() {
        return TestProvider.create(ConfigDefault.MODEL).generateCartesian(ConfigDefault.F_TEST, ParamsCartesian.create()
                .feedback()
                .label("Cartesian"));
    }

    static Iterable<Object[]> genStatic() {
        return TestProvider.create(ConfigDefault.MODEL).generateStatic(ConfigDefault.F_TEST, ParamsStatic.create()
                .feedback()
                .label("Static"));
    }

    static Iterable<Object[]> genStaticAll() {
        return TestProvider.create(ConfigDefault.MODEL).generateStatic(ConfigDefault.F_TEST, ParamsStatic.create()
                .feedback()
                .testSuites("ALL")
                .label("Static  - All"));
    }

    static Iterable<Object[]> genStaticSelected() {
        return TestProvider.create(ConfigDefault.MODEL).generateStatic(ConfigDefault.F_TEST, ParamsStatic.create()
                .feedback()
                .testSuites(new String[]{"suite1"})
                .label("Static - Selected"));
    }

    static Iterable<Object[]> genNWiseTest() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .label("NWise / Feedback"));
    }

    @ParameterizedTest
    @MethodSource("genRandomQuantitySingle")
    void genRandomQuantitySingle(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomQuantityShort")
    void genRandomQuantityShort(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomQuantityLong")
    void genRandomQuantityLong(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandom")
    void genRandom(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomAdaptive")
    void genRandomAdaptive(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genRandomDuplicates")
    void genRandomDuplicates(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWise")
    void genNWise(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseN")
    void genNWiseN(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseCoverage")
    void genNWiseCoverage(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseConstraintsNone")
    void genNWiseConstraintsNone(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseConstraintsSelected")
    void genNWiseConstraintsSelected(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseChoicesSelected")
    void genNWiseChoicesSelected(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genPairwise")
    void genPairwise(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genCartesian")
    void genCartesian(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genStatic")
    void genStatic(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genStaticAll")
    void genStaticAll(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genStaticSelected")
    void genStaticSelected(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFTest(arg1, arg2, arg3, testHandle);
    }

    @ParameterizedTest
    @MethodSource("genNWiseTest")
    void genNWiseNTest(int arg1, int arg2, int arg3, TestHandle testHandle) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3 + ", testHandle = " + testHandle);
        Oracle.validateFeedbackFTest(arg1, arg2, arg3, testHandle);
    }

    @Test
    void genNWiseMap() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("constraints", new String[]{"constraint1"});
        config.put("choices", new HashMap<>(){{put("arg1", new String[]{"choice1", "choice2", "choice3"});}});
        config.put("coverage", "100");
        config.put("n", "3");
        config.put("feedback", "true");
        config.put("label", "NWise / Map");

        for (Object[] chunk : testProvider.generateNWise(ConfigDefault.F_TEST, config)) {
            System.out.println(Arrays.toString(chunk));
            Oracle.validateMapFTest((int) chunk[0], (int) chunk[1], (int) chunk[2], (TestHandle) chunk[3]);
        }
    }

}
