package com.ecfeed.runner.demo;

import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.runner.ConfigDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {
    private static ConfigDefault.Stage stage = ConfigDefault.Stage.LOCAL_BASIC;

    private boolean testLocal() {

        return stage == ConfigDefault.Stage.LOCAL_BASIC || stage == ConfigDefault.Stage.LOCAL_TEAM;
    }

    @Test
    @EnabledIf("testLocal")
    void wrongLicenseTest() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", ConfigDefault.getGeneratorAddress(stage));
        configProvider.put("keyStorePath", ConfigDefault.getKeystorePath(stage));

        var provider = TestProvider.create(ConfigDefault.getModel(ConfigDefault.Stage.LOCAL_BASIC), configProvider);

        try {
            provider.generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create().typesDefinitionsSource(Source.class));
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().toLowerCase().contains("please upgrade"));
        }
    }

    @Test
    @EnabledIf("testLocal")
    void correctLicenseTest() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", ConfigDefault.getGeneratorAddress(stage));
        configProvider.put("keyStorePath", ConfigDefault.getKeystorePath(stage));

        var provider = TestProvider.create(ConfigDefault.getModel(ConfigDefault.Stage.LOCAL_TEAM), configProvider);

        provider.generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create().typesDefinitionsSource(Source.class));
    }

    @Test
    void classEmptyTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("test", ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().toLowerCase().contains("class is non-existent"));
        }
    }

    @Test
    void classNotFoundTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("element.test", ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().toLowerCase().contains("does not contain the requested class"));
        }
    }

    @Test
    void classCorruptedSignatureTest1() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("TestStructure.generate(", ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().toLowerCase().contains("method signature is corrupted"));
        }
    }

    @Test
    void classCorruptedSignatureTest2() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("TestStructure.generate)", ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("method signature is corrupted"));
        }
    }

    @Test
    void classMissingMethodTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("TestStructure.test", ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("does not contain the requested method"));
        }
    }

    @Test
    void wrongModelTest() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", ConfigDefault.getGeneratorAddress(stage));
        configProvider.put("keyStorePath", ConfigDefault.getKeystorePath(stage));

        var provider = TestProvider.create("XYZ", configProvider);

        try {
            provider.generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("does not exist"));
        }
    }

    @Test
    void wrongGeneratorTest() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", "https://XYZ");
        configProvider.put("keyStorePath", ConfigDefault.getKeystorePath(stage));

        var provider = TestProvider.create(ConfigDefault.getModel(stage), configProvider);

        try {
            provider.generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create());
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().toLowerCase().contains("generator address might be erroneous"));
        }
    }

    @Test
    void wrongKeyStoreTest() {
        Map<String, String> configProvider = new HashMap<>();
        configProvider.put("generatorAddress", ConfigDefault.getGeneratorAddress(stage));
        configProvider.put("keyStorePath", "xyz");

        try {
            TestProvider.create(ConfigDefault.getModel(stage), configProvider);
            Assertions.fail("An exception should be thrown!");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("does not exist"));
        }
    }
}
