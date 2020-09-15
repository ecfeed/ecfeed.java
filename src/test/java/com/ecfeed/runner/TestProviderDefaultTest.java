package com.ecfeed.runner;

import com.ecfeed.runner.constant.Template;
import com.ecfeed.runner.design.TestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestProviderDefaultTest {


    @Nested
    @DisplayName("Set up")
    class SetUp {

        @Test
        @DisplayName("Get model name A")
        void getModelA() {
            TestProvider testProvider = EcFeedFactory.getTestProvider("model");

            assertEquals("model", testProvider.getModel(), "The model name is incorrect");
        }

        @Test
        @DisplayName("Get model name B")
        void getModelB() {
            TestProvider testProvider = EcFeedFactory.getTestProvider("model", new HashMap<>());

            assertEquals("model", testProvider.getModel(),
                    "The model name is incorrect");
        }

        @Test
        @DisplayName("Get generator address")
        void getGeneratorAddress() {
            Map<String, String> config = new HashMap<>();
            config.put("generatorAddress", "testAddress");

            TestProvider testProvider = EcFeedFactory.getTestProvider("model", config);

            assertEquals("testAddress", testProvider.getGeneratorAddress(),
                    "The generator address is incorrect");
        }

        @Test
        @DisplayName("Get generator address (default)")
        void getGeneratorAddressDefault() {
            TestProvider testProvider = EcFeedFactory.getTestProvider("model");

            assertEquals(Config.Value.generatorAddress, testProvider.getGeneratorAddress(),
                    "The generator address is incorrect");
        }

        @Test
        @DisplayName("Get keystore password")
        void getKeyStorePassword() {
            Map<String, String> config = new HashMap<>();
            config.put("keyStorePassword", "changeit");

            TestProvider testProvider = EcFeedFactory.getTestProvider("model", config);

            assertEquals("changeit", testProvider.getKeyStorePassword(),
                    "The keystore password is incorrect");
        }

        @Test
        @DisplayName("Get keystore password (default)")
        void getKeyStorePasswordDefault() {
            TestProvider testProvider = EcFeedFactory.getTestProvider("model");

            assertEquals(Config.Value.keyStorePassword, testProvider.getKeyStorePassword(),
                    "The keystore password is incorrect");
        }

        @Test
        @DisplayName("Get keystore path")
        void getKeyStorePath() {
            Map<String, String> config = new HashMap<>();
            config.put("keyStorePath", "src/test/resources/security.p12");

            TestProvider testProvider = EcFeedFactory.getTestProvider("model", config);

            assertTrue(testProvider.getKeyStorePath().toString().endsWith("src/test/resources/security.p12"),
                    "The keystore path is incorrect");
        }

        @Test
        @DisplayName("Get keystore path (invalid path)")
        void getKeyStorePathInvalidPath() {
            Map<String, String> config = new HashMap<>();
            config.put("keyStorePath", "test");

            assertThrows(IllegalArgumentException.class, () -> EcFeedFactory.getTestProvider("model", config),
                    "The path is invalid, and therefore, an exception should be thrown");
        }

        @Test
        @DisplayName("Get keystore path (invalid password)")
        void getKeyStorePathInvalidPassword() {
            Map<String, String> config = new HashMap<>();
            config.put("keyStorePassword", "test");
            config.put("keyStorePath", "src/test/resources/security.p12");

            assertThrows(IllegalArgumentException.class, () -> EcFeedFactory.getTestProvider("model", config),
                    "The password is invalid, and therefore, an exception should be thrown");
        }

        @Test
        @DisplayName("Export")
        void export() {
            TestProvider testProvider = EcFeedFactory.getTestProvider("MDWG-I8K7-BXRY-JTFR-JEDQ");

            Map<String, Object> config = new HashMap<>();

            for (Object[] chunk : testProvider.streamNWise("com.example.test.LoanDecisionTest2.generateCustomerData", config)) {
                System.out.println(Arrays.toString(chunk));
            };
            for (String chunk : testProvider.exportNWise("com.example.test.LoanDecisionTest2.generateCustomerData", Template.JSON, config)) {
                System.out.println(chunk);
            };
        }

    }
}
