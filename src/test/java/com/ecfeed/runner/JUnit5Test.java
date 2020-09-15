package com.ecfeed.runner;

import com.ecfeed.runner.design.TestProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

    static Iterator<Object[]> testProvider() {
        TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");
        Map<String, Object> config = new HashMap<>();
        return testProvider.streamNWise("com.example.test.LoanDecisionTest2.generateCustomerData", config);
    }

    @ParameterizedTest
    @MethodSource("testProvider")
    void testProvider(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);

    }

}
