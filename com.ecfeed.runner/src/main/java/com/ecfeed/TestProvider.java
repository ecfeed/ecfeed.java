package com.ecfeed;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.data.DataConnection;
import com.ecfeed.data.DataSession;
import com.ecfeed.helper.HelperConnection;
import com.ecfeed.params.*;
import com.ecfeed.queue.IterableTestQueue;
import com.ecfeed.type.TypeExport;
import com.ecfeed.type.TypeGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestProvider {

    private static int ITERATOR_TIMEOUT = 1000;
    private static int ITERATOR_TIMEOUT_STEP = 100;

    private String model;
    private DataConnection connection;

    private TestProvider(String model, Map<String, String> config) {

        setup(model, config);
    }

    public static TestProvider create(String model) {

        return new TestProvider(model, new HashMap<>());
    }

    public static TestProvider create(String model, Map<String, String> config) {

        return new TestProvider(model, config);
    }

    private void setup(String model, Map<String, String> config) {

        this.model = model;
        this.connection = DataConnection.create(
                setupExtractGeneratorAddress(config),
                setupExtractKeyStorePath(config),
                setupExtractKeyStorePassword(config)
        );

    }

    private String setupExtractGeneratorAddress(Map<String, String> config) {
        String value = config.get(ConfigDefault.Key.setupGeneratorAddress);

        return value != null ? value : ConfigDefault.Value.generatorAddress;
    }

    private String setupExtractKeyStorePassword(Map<String, String> config) {
        String value = config.get(ConfigDefault.Key.setupKeyStorePassword);

        return value != null ? value : ConfigDefault.Value.keyStorePassword;
    }

    private Path setupExtractKeyStorePath(Map<String, String> config) {
        String value = config.get(ConfigDefault.Key.setupKeyStorePath);

        if (value != null) {
            return getKeyStore(value);
        } else {
            return getKeyStoreDefault();
        }
    }

    private Path getKeyStore(String address) {

        return getKeyStorePath(address);
    }

    private Path getKeyStoreDefault() {

        for (String address : ConfigDefault.Value.keyStorePath) {
            try {
                return getKeyStore(address);
            } catch (IllegalArgumentException ignored) {
            }
        }

        throw new IllegalArgumentException("The keystore could not be loaded");
    }

    private Path getKeyStorePath(String keyStoreAddress) {
        Path keyStorePath = Paths.get(keyStoreAddress);

        if (!Files.exists(keyStorePath)) {
            throw new IllegalArgumentException("The keystore does not exist: " + keyStorePath.toAbsolutePath());
        }

        if (!Files.isReadable(keyStorePath)) {
            throw new IllegalArgumentException("The keystore is not readable: " + keyStorePath.toAbsolutePath());
        }

        if (!Files.isRegularFile(keyStorePath)) {
            throw new IllegalArgumentException("The keystore file type is erroneous: " + keyStorePath.toAbsolutePath());
        }

        return keyStorePath;
    }

    public String getModel() {

        return model;
    }

    public String getAddress() {

        return this.connection.getHttpAddress();
    }

    public Path getKeyStorePath() {

        return this.connection.getKeyStorePath();
    }

    public Iterable<String> export(String method, TypeGenerator generator, TypeExport typeExport, Map<String, Object> properties) {
        ConfigDefault.processUserParameters(properties);
        ConfigDefault.validateUserParameters(properties);

        IterableTestQueue<String> iterator = IterableTestQueue.createForExport();

        DataSession dataSession = DataSession.create(this.connection, this.model, method, generator);
        dataSession.setGeneratorOptions(properties);
        dataSession.setTemplate(typeExport);

        new Thread(() -> {
            try {
                HelperConnection.processChunkStream(iterator, HelperConnection.getChunkStreamForTestData(dataSession));
            } finally {
                iterator.terminate();
            }
        }).start();

        validate(iterator, generator);

        return iterator;
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return export(method, TypeGenerator.NWise, typeExport, updatedProperties);
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, ParamsNWise properties) {

        return exportNWise(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport) {

        return exportNWise(method, typeExport, ParamsNWise.create());
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return export(method, TypeGenerator.NWise, typeExport, updatedProperties);
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport, ParamsPairwise properties) {

        return exportPairwise(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport) {

        return exportPairwise(method, typeExport, ParamsPairwise.create());
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, TypeGenerator.Cartesian, typeExport, updatedProperties);
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport, ParamsCartesian properties) {

        return exportCartesian(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport) {

        return exportCartesian(method, typeExport, ParamsCartesian.create());
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parLength, ConfigDefault.Value.parLength + "");
        addProperty(updatedProperties, ConfigDefault.Key.parAdaptive, ConfigDefault.Value.parAdaptive + "");
        addProperty(updatedProperties, ConfigDefault.Key.parDuplicates, ConfigDefault.Value.parDuplicates + "");

        return export(method, TypeGenerator.Random, typeExport, updatedProperties);
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport, ParamsRandom properties) {

        return exportRandom(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport) {

        return exportRandom(method, typeExport, ParamsRandom.create());
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, TypeGenerator.Static, typeExport, updatedProperties);
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport, ParamsStatic properties) {

        return exportStatic(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport) {

        return exportStatic(method, typeExport, ParamsStatic.create());
    }

    public Iterable<Object[]> generate(String method, TypeGenerator generator, Map<String, Object> properties) {
        ConfigDefault.processUserParameters(properties);
        ConfigDefault.validateUserParameters(properties);

        DataSession dataSession = DataSession.create(this.connection, this.model, method, generator);
        dataSession.setGeneratorOptions(properties);

        IterableTestQueue<Object[]> iterator = IterableTestQueue.createForStream(dataSession);

        new Thread(() -> {
            try {
                HelperConnection.processChunkStream(iterator, HelperConnection.getChunkStreamForTestData(dataSession));
            } finally {
                iterator.terminate();
            }
        }).start();

        validate(iterator, generator);

        return iterator;
    }

    private void validate(IterableTestQueue<?> iterator, TypeGenerator generator) {
        int timeout = 0;

        do {
            if (iterator.hasNext()) {
                return;
            }

            timeout += ITERATOR_TIMEOUT_STEP;

            try {
                Thread.sleep(ITERATOR_TIMEOUT_STEP);
            } catch (InterruptedException e) { }

        } while (timeout < ITERATOR_TIMEOUT);

        if (generator == TypeGenerator.Static) {
            throw new IllegalArgumentException("Empty test set error! " +
                    "Please check if the name of the requested test suite is correct.");
        } else {
            throw new IllegalArgumentException("Will check in a while");
        }
    }

    public Iterable<Object[]> generateNWise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return generate(method, TypeGenerator.NWise, updatedProperties);
    }

    public Iterable<Object[]> generateNWise(String method, ParamsNWise properties) {

        return generateNWise(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateNWise(String method) {

        return generateNWise(method, ParamsNWise.create());
    }

    public Iterable<Object[]> generatePairwise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return generate(method, TypeGenerator.NWise, updatedProperties);
    }

    public Iterable<Object[]> generatePairwise(String method, ParamsPairwise properties) {

        return generatePairwise(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generatePairwise(String method) {

        return generatePairwise(method, ParamsPairwise.create());
    }

    public Iterable<Object[]> generateCartesian(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, TypeGenerator.Cartesian, updatedProperties);
    }

    public Iterable<Object[]> generateCartesian(String method, ParamsCartesian properties) {

        return generateCartesian(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateCartesian(String method) {

        return generateCartesian(method, ParamsCartesian.create());
    }

    public Iterable<Object[]> generateRandom(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parLength, ConfigDefault.Value.parLength + "");
        addProperty(updatedProperties, ConfigDefault.Key.parAdaptive, ConfigDefault.Value.parAdaptive + "");
        addProperty(updatedProperties, ConfigDefault.Key.parDuplicates, ConfigDefault.Value.parDuplicates + "");

        return generate(method, TypeGenerator.Random, updatedProperties);
    }

    public Iterable<Object[]> generateRandom(String method, ParamsRandom properties) {

        return generateRandom(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateRandom(String method) {

        return generateRandom(method, ParamsRandom.create());
    }

    public Iterable<Object[]> generateStatic(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, TypeGenerator.Static, updatedProperties);
    }

    public Iterable<Object[]> generateStatic(String method, ParamsStatic properties) {

        return generateStatic(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateStatic(String method) {

        return generateStatic(method, ParamsStatic.create());
    }

    public void validateConnection() {

        HelperConnection.validateConnection(this.connection);
    }

    public List<String> getArgumentNames(String method) {

        return Arrays.asList(HelperConnection.sendMockRequest(this.connection, this.model, method).getArgumentNames());
    }

    public List<String> getArgumentTypes(String method) {

        return Arrays.asList(HelperConnection.sendMockRequest(this.connection, this.model, method).getArgumentTypes());
    }

    private void addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

}
