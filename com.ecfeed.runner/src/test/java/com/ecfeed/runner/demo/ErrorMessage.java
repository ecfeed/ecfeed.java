package com.ecfeed.runner.demo;

import com.ecfeed.params.ParamsNWise;
import com.ecfeed.runner.ConfigDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorMessage {
    private static ConfigDefault.Stage stage = ConfigDefault.Stage.LOCAL;

    @Test
    void wrongLicenseTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create().typesDefinitionsSource(Source.class));
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("Please upgrade"));
        }
    }

    @Test
    void classEmptyTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("test", ParamsNWise.create());
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("class name is non-existent"));
        }
    }

    @Test
    void classNotFoundTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("element.test", ParamsNWise.create());
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("does not contain the requested class"));
        }
    }

    @Test
    void classCorruptedSignatureTest1() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("TestStructure.generate(", ParamsNWise.create());
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("method signature is corrupted"));
        }
    }

    @Test
    void classCorruptedSignatureTest2() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("TestStructure.generate)", ParamsNWise.create());
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("method signature is corrupted"));
        }
    }

    @Test
    void classMissingMethodTest() {
        var provider = ConfigDefault.getTestProviderRemote(stage);

        try {
            provider.generateNWise("TestStructure.test", ParamsNWise.create());
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getMessage().contains("does not contain the requested method"));
        }
    }
}
