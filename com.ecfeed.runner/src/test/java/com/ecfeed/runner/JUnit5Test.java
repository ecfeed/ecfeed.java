package com.ecfeed.runner;

import com.ecfeed.TestProvider;
import com.ecfeed.TypeExport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnit5Test {

    private static final String model = "ZCPH-DFYI-R7R7-R6MM-89L8";
    private static final String method = "com.example.test.LoanDecisionTest2.generateCustomerData";

    enum Gender {
        MALE, FEMALE
    }

    enum ID {
        PASSPORT, DRIVERS_LICENSE, PERSONAL_ID
    }

    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create(model);
        Map<String, Object> config = new HashMap<>();
        return testProvider.generateNWise(method, config);
    }

    static Iterable<Object[]> testProviderCartesian() {
        TestProvider testProvider = TestProvider.create(model);
        Map<String, Object> config = new HashMap<>();
        return testProvider.generateCartesian(method, config);
    }

    static Iterable<Object[]> testProviderRandom() {
        TestProvider testProvider = TestProvider.create(model);
        Map<String, Object> config = new HashMap<>();
        return testProvider.generateRandom(method, config);
    }

    static Iterable<Object[]> testProviderStatic() {
        TestProvider testProvider = TestProvider.create(model);
        Map<String, Object> config = new HashMap<>();
        return testProvider.generateStatic(method, config);
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
    @DisplayName("Export raw")
    void exportTypeRaw() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (String chunk : testProvider.exportNWise(method, TypeExport.Raw, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export xml")
    void exportTypeXml() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (String chunk : testProvider.exportNWise(method, TypeExport.XML, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export json")
    void exportTypeJson() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export csv")
    void exportTypeCsv() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (String chunk : testProvider.exportNWise(method, TypeExport.CSV, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export gherkin")
    void exportTypeGherkin() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (String chunk : testProvider.exportNWise(method, TypeExport.Gherkin, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("NWise")
    void exportNWise() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        String[] constraints = new String[]{ "gender" };
        config.put("constraints", constraints);

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });
        config.put("choices", choices);

        for (Object[] chunk : testProvider.generateNWise(method, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Cartesian")
    void exportCartesian() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (Object[] chunk : testProvider.generateCartesian(method, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportCartesian(method, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Random")
    void exportRandom() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        for (Object[] chunk : testProvider.generateRandom(method, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportRandom(method, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Static")
    void exportStatic() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();

        String[] constraints = new String[]{ "tests" };
        config.put("testSuites", constraints);

        for (Object[] chunk : testProvider.generateStatic(method, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportStatic(method, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Get method types")
    void getMethodTypes() {
        TestProvider testProvider = TestProvider.create(model);

        System.out.println(testProvider.getMethodTypes(method));
    }

    @Test
    @DisplayName("Get method names")
    void getMethodNames() {
        TestProvider testProvider = TestProvider.create(model);

        System.out.println(testProvider.getMethodNames(method));
    }

    @Test
    @DisplayName("Validate")
    void validate() {
        TestProvider testProvider = TestProvider.create(model);

        testProvider.validateConnection();
    }

    @Test
    @DisplayName("Get model")
    void getModel() {
        TestProvider testProvider = TestProvider.create(model);

        assertEquals(model, testProvider.getModel(),
                "The default name of the model is erroneous");
    }

    @Test
    @DisplayName("Get model (custom)")
    void getModelCustom() {
        TestProvider testProvider = TestProvider.create("testModel");

        assertEquals("testModel", testProvider.getModel(),
                "The custom name of the model is erroneous");
    }

    @Test
    @DisplayName("Get generator address")
    void getGeneratorAddress() {
        TestProvider testProvider = TestProvider.create(model);

        assertEquals("https://develop-gen.ecfeed.com", testProvider.getGeneratorAddress(),
                "The default generator address is erroneous");
    }

    @Test
    @DisplayName("Get generator address (custom)")
    void getGeneratorAddressCustom() {
        Map<String, String> config = new HashMap<>();
        config.put("generatorAddress", "testAddress");
        TestProvider testProvider = TestProvider.create(model, config);

        assertEquals("testAddress", testProvider.getGeneratorAddress(),
                "The custom generator address is erroneous");
    }

    @Test
    @DisplayName("Get keystore path")
    void getKeyStorePath() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create(model, config);

        assertEquals("src/test/resources/security.p12", testProvider.getKeyStorePath().toString(),
                "The keystore path is erroneous");
    }

    @Test
    @DisplayName("Error - generator address")
    void errorGeneratorAddress() {
        Map<String, String> config = new HashMap<>();
        config.put("generatorAddress", "testAddress");
        config.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create(model, config);

        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - model name")
    void errorModelName() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create("testModel", config);

        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - method name")
    void errorMethodName() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create(model, config);

        for (String chunk : testProvider.exportNWise("testMethod", TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - keystore address")
    void errorKeyStoreAddress() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePath", "testPath");
        TestProvider testProvider = TestProvider.create(model, config);

        testProvider.validateConnection();
    }

    @Test
    @DisplayName("Error - keystore password")
    void errorKeyStorePassword() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePassword", "testPassword");
        config.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create(model, config);

        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - missing client certificate")
    void errorMissingClientCertificate() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePath", "src/test/resources/securityNoClient.p12");
        TestProvider testProvider = TestProvider.create(model, config);

        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - missing server certificate")
    void errorMissingServeCertificate() {
        Map<String, String> config = new HashMap<>();
        config.put("keyStorePath", "src/test/resources/securityNoServer.p12");
        TestProvider testProvider = TestProvider.create(model, config);

        for (String chunk : testProvider.exportNWise(method, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - invalid user parameters")
    void errorInvalidUserParameters() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();
        config.put("error", "error");

        testProvider.exportNWise(method, TypeExport.JSON, config);
    }
}
