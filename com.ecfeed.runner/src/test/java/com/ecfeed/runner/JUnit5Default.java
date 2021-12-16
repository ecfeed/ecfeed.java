package com.ecfeed.runner;

import com.ecfeed.TestProvider;
import com.ecfeed.params.*;
import com.ecfeed.type.TypeExport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JUnit5Default {

    enum Gender {
        MALE, FEMALE
    }

    enum ID {
        PASSPORT, DRIVERS_LICENSE, PERSONAL_ID
    }

    static Iterable<Object[]> testProviderNWiseFeedback() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_LOAN_2, ParamsNWise.create().feedback());
    }

    static Iterable<Object[]> testProviderNWise() {
        return TestProvider.create(ConfigDefault.MODEL).generateNWise(ConfigDefault.F_LOAN_2);
    }

    static Iterable<Object[]> testProviderPairwise() {
        return TestProvider.create(ConfigDefault.MODEL).generatePairwise(ConfigDefault.F_LOAN_2);
    }

    static Iterable<Object[]> testProviderCartesian() {
        return TestProvider.create(ConfigDefault.MODEL).generateCartesian(ConfigDefault.F_LOAN_2);
    }

    static Iterable<Object[]> testProviderRandom() {
        return TestProvider.create(ConfigDefault.MODEL).generateRandom(ConfigDefault.F_LOAN_2);
    }

    static Iterable<Object[]> testProviderStatic() {
        return TestProvider.create(ConfigDefault.MODEL).generateStatic(ConfigDefault.F_LOAN_2);
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

    @ParameterizedTest
    @MethodSource("testProviderPairwise")
    void testProviderPairwise(String name, String firstName, Gender gender, int age, String id, ID type) {
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
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.Raw, ParamsNWise.create())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export xml")
    void exportTypeXml() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.XML, ParamsNWise.create())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export json")
    void exportTypeJson() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.JSON, ParamsNWise.create())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export csv")
    void exportTypeCsv() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.CSV, ParamsNWise.create())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Export gherkin")
    void exportTypeGherkin() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.Gherkin, ParamsNWise.create())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("NWise")
    void exportNWise() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("constraints", constraints);
        config.put("choices", choices);
        config.put("coverage", "100");
        config.put("n", "3");

        for (Object[] chunk : testProvider.generateNWise(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("NWise Param")
    void exportNWiseParam() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        ParamsNWise config = ParamsNWise.create()
                .constraints(constraints)
                .choices(choices)
                .coverage(100)
                .n(3);

        for (Object[] chunk : testProvider.generateNWise(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Pairwise")
    void exportPairwise() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("constraints", constraints);
        config.put("choices", choices);
        config.put("coverage", "100");

        for (Object[] chunk : testProvider.generatePairwise(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportPairwise(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Pairwise Param")
    void exportPairwiseParam() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        ParamsPairwise config = ParamsPairwise.create()
                .constraints(constraints)
                .choices(choices)
                .coverage(100);

        for (Object[] chunk : testProvider.generatePairwise(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportPairwise(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Cartesian")
    void exportCartesian() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("constraints", constraints);
        config.put("choices", choices);

        for (Object[] chunk : testProvider.generateCartesian(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportCartesian(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Cartesian Param")
    void exportCartesianParam() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        ParamsCartesian config = ParamsCartesian.create()
                .constraints(constraints)
                .choices(choices);

        for (Object[] chunk : testProvider.generateCartesian(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportCartesian(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Random")
    void exportRandom() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("constraints", constraints);
        config.put("choices", choices);
        config.put("length", "25");
        config.put("adaptive", "false");
        config.put("duplicates", "true");

        for (Object[] chunk : testProvider.generateRandom(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportRandom(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Random Param")
    void exportRandomParam() {
        String[] constraints = new String[]{ "gender" };

        Map<String, String[]> choices = new HashMap<>();
        choices.put("firstName", new String[]{ "male:short" });

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        ParamsRandom config = ParamsRandom.create()
                .constraints(constraints)
                .choices(choices)
                .length(25)
                .adaptive(true)
                .duplicates(true);

        for (Object[] chunk : testProvider.generateRandom(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportRandom(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Static")
    void exportStatic() {
        String[] testSuites = new String[]{ "default suite" };

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("testSuites", testSuites);

        for (Object[] chunk : testProvider.generateStatic(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportStatic(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Static Params")
    void exportStaticParams() {
        String[] testSuites = new String[]{ "default suite" };

        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        ParamsStatic config = ParamsStatic.create()
                .testSuites(testSuites);

        for (Object[] chunk : testProvider.generateStatic(ConfigDefault.F_LOAN_2, config)) {
            System.out.println(Arrays.toString(chunk));
        }
        for (String chunk : testProvider.exportStatic(ConfigDefault.F_LOAN_2, TypeExport.JSON, config)) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Get method types")
    void getMethodTypes() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        System.out.println(testProvider.getArgumentTypes(ConfigDefault.F_LOAN_2));
    }

    @Test
    @DisplayName("Get method names")
    void getMethodNames() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        System.out.println(testProvider.getArgumentNames(ConfigDefault.F_LOAN_2));
    }

    @Test
    @DisplayName("Validate")
    void validate() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        testProvider.validateConnection();
    }

    @Test
    @DisplayName("Get model")
    void getModel() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        assertEquals(ConfigDefault.MODEL, testProvider.getModel(),
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
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        assertTrue("https://gen.ecfeed.com".equalsIgnoreCase(testProvider.getAddress()) || "https://develop-gen.ecfeed.com".equalsIgnoreCase(testProvider.getAddress()),
                "The default generator address is erroneous");
    }

    @Test
    @DisplayName("Get generator address (custom)")
    void getGeneratorAddressCustom() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", "testAddress");
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL, configProvider);

        assertEquals("testAddress", testProvider.getAddress(),
                "The custom generator address is erroneous");
    }

    @Test
    @DisplayName("Get keystore path")
    void getKeyStorePath() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePath", "src/test/resources/security.p12");
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL, configProvider);

        assertEquals("src/test/resources/security.p12", testProvider.getKeyStorePath().toString(),
                "The keystore path is erroneous");
    }

    @Test
    @DisplayName("Error - generator address")
    void errorGeneratorAddress() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", "testAddress");
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL, configProvider);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - model name")
    void errorModelName() {
        TestProvider testProvider = TestProvider.create("testModel");

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - method name")
    void errorMethodName() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        for (String chunk : testProvider.exportNWise("testMethod", TypeExport.JSON, new HashMap<>())) {
            System.out.println(chunk);
        }
    }

    @Test
    @DisplayName("Error - keystore password")
    void errorKeyStorePassword() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePassword", "testPassword");

        assertThrows(IllegalArgumentException.class, () -> TestProvider.create(ConfigDefault.MODEL, configProvider));
    }

    @Test
    @DisplayName("Error - missing client certificate")
    void errorMissingClientCertificate() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePath", "src/test/resources/securityNoClient.p12");

        assertThrows(IllegalArgumentException.class, () -> TestProvider.create(ConfigDefault.MODEL, configProvider));
    }

    @Test
    @DisplayName("Error - missing server certificate")
    void errorMissingServeCertificate() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("keyStorePath", "src/test/resources/securityNoServer.p12");

        assertThrows(IllegalArgumentException.class, () -> TestProvider.create(ConfigDefault.MODEL, configProvider));
    }

    @Test
    @DisplayName("Error - invalid user parameters")
    void errorInvalidUserParameters() {
        TestProvider testProvider = TestProvider.create(ConfigDefault.MODEL);

        Map<String, Object> config = new HashMap<>();
        config.put("error", "error");

        assertThrows(IllegalArgumentException.class, () -> testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.JSON, config));
    }
}