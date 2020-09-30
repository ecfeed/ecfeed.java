package com.ecfeed.runner;

import com.ecfeed.junit.TestProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;

public class Tutorial {

    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL");
        return testProvider.generateNWise("QuickStart.test", new HashMap<>());
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(int arg1, int arg2, int arg3) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
    }

    public static void main(String[] args) {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL",configProvider);
        testProvider.generateNWise("QuickStart", new HashMap<>());
    }
}
