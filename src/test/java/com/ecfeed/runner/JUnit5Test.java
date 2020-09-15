package com.ecfeed.runner;

import com.ecfeed.runner.constant.Template;
import com.ecfeed.runner.design.TestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JUnit5Test {

    enum Gender {
        MALE, FEMALE
    }

    enum ID {
        PASSPORT, DRIVERS_LICENSE, PERSONAL_ID
    }

    static Iterator<Object[]> testProviderNWise() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");
        Map<String, Object> config = new HashMap<>();
        return testProvider.streamNWise("com.example.test.LoanDecisionTest2.generateCustomerData", config);
    }

    static Iterator<Object[]> testProviderCartesian() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");
        Map<String, Object> config = new HashMap<>();
        return testProvider.streamCartesian("com.example.test.LoanDecisionTest2.generateCustomerData", config);
    }

    static Iterator<Object[]> testProviderRandom() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");
        Map<String, Object> config = new HashMap<>();
        return testProvider.streamRandom("com.example.test.LoanDecisionTest2.generateCustomerData", config);
    }

    static Iterator<Object[]> testProviderStatic() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");
        Map<String, Object> config = new HashMap<>();
        return testProvider.streamStatic("com.example.test.LoanDecisionTest2.generateCustomerData", config);
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

    @ParameterizedTest
    @MethodSource("testProviderCartesian")
    void testProviderCartesian(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

    @ParameterizedTest
    @MethodSource("testProviderRandom")
    void testProviderRandom(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

    @ParameterizedTest
    @MethodSource("testProviderStatic")
    void testProviderStatic(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

    @Test
    @DisplayName("NWise")
    void exportNWise() {//choices/constraints/suites
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");

        Map<String, Object> config = new HashMap<>();

        for (Object[] chunk : testProvider.streamNWise("com.example.test.LoanDecisionTest2.generateCustomerData", config)) {
            System.out.println(Arrays.toString(chunk));
        };
        for (String chunk : testProvider.exportNWise("com.example.test.LoanDecisionTest2.generateCustomerData", Template.JSON, config)) {
            System.out.println(chunk);
        };
    }

    @Test
    @DisplayName("Cartesian")
    void exportCartesian() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");

        Map<String, Object> config = new HashMap<>();

        for (Object[] chunk : testProvider.streamCartesian("com.example.test.LoanDecisionTest2.generateCustomerData", config)) {
            System.out.println(Arrays.toString(chunk));
        };
        for (String chunk : testProvider.exportCartesian("com.example.test.LoanDecisionTest2.generateCustomerData", Template.JSON, config)) {
            System.out.println(chunk);
        };
    }

    @Test
    @DisplayName("Random")
    void exportRandom() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");

        Map<String, Object> config = new HashMap<>();

        for (Object[] chunk : testProvider.streamRandom("com.example.test.LoanDecisionTest2.generateCustomerData", config)) {
            System.out.println(Arrays.toString(chunk));
        };
        for (String chunk : testProvider.exportRandom("com.example.test.LoanDecisionTest2.generateCustomerData", Template.JSON, config)) {
            System.out.println(chunk);
        };
    }

    @Test
    @DisplayName("Static")
    void exportStatic() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");

        Map<String, Object> config = new HashMap<>();

        for (Object[] chunk : testProvider.streamStatic("com.example.test.LoanDecisionTest2.generateCustomerData", config)) {
            System.out.println(Arrays.toString(chunk));
        };
        for (String chunk : testProvider.exportStatic("com.example.test.LoanDecisionTest2.generateCustomerData", Template.JSON, config)) {
            System.out.println(chunk);
        };
    }
}
