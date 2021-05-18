package com.ecfeed;

import com.ecfeed.data.ConnectionData;
import com.ecfeed.data.SessionData;
import com.ecfeed.helper.ConnectionHelper;
import com.ecfeed.params.*;
import com.ecfeed.parser.ChunkParserExport;
import com.ecfeed.parser.ChunkParserStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestProvider {

    private String model;
    private ConnectionData connection;

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
        this.connection = ConnectionData.create(
                setupExtractGeneratorAddress(config),
                setupExtractKeyStorePath(config),
                setupExtractKeyStorePassword(config)
        );

    }

    private String setupExtractGeneratorAddress(Map<String, String> config) {
        String value = config.get(Config.Key.setupGeneratorAddress);

        return value != null ? value : Config.Value.generatorAddress;
    }

    private String setupExtractKeyStorePassword(Map<String, String> config) {
        String value = config.get(Config.Key.setupKeyStorePassword);

        return value != null ? value : Config.Value.keyStorePassword;
    }

    private Path setupExtractKeyStorePath(Map<String, String> config) {
        String value = config.get(Config.Key.setupKeyStorePath);

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

        for (String address : Config.Value.keyStorePath) {
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

    public Iterable<String> export(String method, String generatorType, TypeExport typeExport, Map<String, Object> properties) {
        Config.validateUserParameters(properties);

        IterableTestQueue<String> iterator = new IterableTestQueue<>(ChunkParserExport.create());

        SessionData sessionData = SessionData.create(this.connection, this.model, method, generatorType);
        sessionData.setGeneratorOptions(properties);
        sessionData.setTemplate(typeExport);

        new Thread(() -> {
            try {
                ConnectionHelper.processChunkStream(iterator, ConnectionHelper.getChunkStreamForTestData(sessionData));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN + "");
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage + "");

        return export(method, Config.Value.parGenNWise, typeExport, updatedProperties);
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, ParamsNWise properties) {

        return exportNWise(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport) {

        return exportNWise(method, typeExport, new ParamsNWise());
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN + "");
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage + "");

        return export(method, Config.Value.parGenNWise, typeExport, updatedProperties);
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport, ParamsPairwise properties) {

        return exportPairwise(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport) {

        return exportPairwise(method, typeExport, new ParamsPairwise());
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Config.Value.parGenCartesian, typeExport, updatedProperties);
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport, ParamsCartesian properties) {

        return exportCartesian(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport) {

        return exportCartesian(method, typeExport, new ParamsCartesian());
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parLength, Config.Value.parLength + "");
        addProperty(updatedProperties, Config.Key.parAdaptive, Config.Value.parAdaptive + "");
        addProperty(updatedProperties, Config.Key.parDuplicates, Config.Value.parDuplicates + "");

        return export(method, Config.Value.parGenRandom, typeExport, updatedProperties);
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport, ParamsRandom properties) {

        return exportRandom(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport) {

        return exportRandom(method, typeExport, new ParamsRandom());
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Config.Value.parGenStatic, typeExport, updatedProperties);
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport, ParamsStatic properties) {

        return exportStatic(method, typeExport, properties.getParamsMap());
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport) {

        return exportStatic(method, typeExport, new ParamsStatic());
    }

    public Iterable<Object[]> generate(String method, String generator, Map<String, Object> properties) {
        Config.validateUserParameters(properties);

        SessionData sessionData = SessionData.create(this.connection, this.model, method, generator);
        sessionData.setGeneratorOptions(properties);

        IterableTestQueue<Object[]> iterator = new IterableTestQueue<>(ChunkParserStream.create(sessionData));

        new Thread(() -> {
            try {
                ConnectionHelper.processChunkStream(iterator, ConnectionHelper.getChunkStreamForTestData(sessionData));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    public Iterable<Object[]> generateNWise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN + "");
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage + "");

        return generate(method, Config.Value.parGenNWise, updatedProperties);
    }

    public Iterable<Object[]> generateNWise(String method, ParamsNWise properties) {

        return generateNWise(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateNWise(String method) {

        return generateNWise(method, new ParamsNWise());
    }

    public Iterable<Object[]> generatePairwise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN + "");
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage + "");

        return generate(method, Config.Value.parGenNWise, updatedProperties);
    }

    public Iterable<Object[]> generatePairwise(String method, ParamsPairwise properties) {

        return generatePairwise(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generatePairwise(String method) {

        return generatePairwise(method, new ParamsPairwise());
    }

    public Iterable<Object[]> generateCartesian(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, Config.Value.parGenCartesian, updatedProperties);
    }

    public Iterable<Object[]> generateCartesian(String method, ParamsCartesian properties) {

        return generateCartesian(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateCartesian(String method) {

        return generateCartesian(method, new ParamsCartesian());
    }

    public Iterable<Object[]> generateRandom(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parLength, Config.Value.parLength + "");
        addProperty(updatedProperties, Config.Key.parAdaptive, Config.Value.parAdaptive + "");
        addProperty(updatedProperties, Config.Key.parDuplicates, Config.Value.parDuplicates + "");

        return generate(method, Config.Value.parGenRandom, updatedProperties);
    }

    public Iterable<Object[]> generateRandom(String method, ParamsRandom properties) {

        return generateRandom(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateRandom(String method) {

        return generateRandom(method, new ParamsRandom());
    }

    public Iterable<Object[]> generateStatic(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, Config.Value.parGenStatic, updatedProperties);
    }

    public Iterable<Object[]> generateStatic(String method, ParamsStatic properties) {

        return generateStatic(method, properties.getParamsMap());
    }

    public Iterable<Object[]> generateStatic(String method) {

        return generateStatic(method, new ParamsStatic());
    }

    public void validateConnection() {

        ConnectionHelper.validateConnection(this.connection);
    }

    public List<String> getMethodNames(String method) {

        return Arrays.asList(ConnectionHelper.sendMockRequest(this.connection, this.model, method).getMethodNames());
    }

    public List<String> getMethodTypes(String method) {

        return Arrays.asList(ConnectionHelper.sendMockRequest(this.connection, this.model, method).getMethodTypes());
    }
    private void addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }


}
