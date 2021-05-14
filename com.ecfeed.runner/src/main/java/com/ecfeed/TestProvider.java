package com.ecfeed;

import com.ecfeed.data.Connection;
import com.ecfeed.data.SessionData;
import com.ecfeed.helper.CollectionHelper;
import com.ecfeed.helper.ConnectionHelper;
import com.ecfeed.parser.ChunkParserExport;
import com.ecfeed.parser.ChunkParserStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestProvider {

    private String model;
    private Connection connection;

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
        this.connection = Connection.create(
                setupExtractGeneratorAddress(config),
                setupExtractKeyStorePath(config),
                setupExtractKeyStorePassword(config)
        );

    }

    private String setupExtractGeneratorAddress(Map<String, String> config) {
        String value = config.get("generatorAddress");

        return value != null ? value : Config.Value.generatorAddress;
    }

    private String setupExtractKeyStorePassword(Map<String, String> config) {
        String value = config.get("keyStorePassword");

        return value != null ? value : Config.Value.keyStorePassword;
    }

    private Path setupExtractKeyStorePath(Map<String, String> config) {
        String value = config.get("keyStorePath");

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

        CollectionHelper.addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

        return export(method, Config.Value.parGenNWise, typeExport, updatedProperties);
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, Param.ParamsNWise properties) {

        return exportNWise(method, typeExport, properties.getParamMap());
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport) {

        return exportNWise(method, typeExport, new Param.ParamsNWise());
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        CollectionHelper.addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

        return export(method, Config.Value.parGenNWise, typeExport, updatedProperties);
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport, Param.ParamsPairwise properties) {

        return exportPairwise(method, typeExport, properties.getParamMap());
    }

    public Iterable<String> exportPairwise(String method, TypeExport typeExport) {

        return exportPairwise(method, typeExport, new Param.ParamsPairwise());
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Config.Value.parGenCartesian, typeExport, updatedProperties);
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport, Param.ParamsCartesian properties) {

        return exportCartesian(method, typeExport, properties.getParamMap());
    }

    public Iterable<String> exportCartesian(String method, TypeExport typeExport) {

        return exportCartesian(method, typeExport, new Param.ParamsCartesian());
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        CollectionHelper.addProperty(updatedProperties, Config.Key.parLength, Config.Value.parLength);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parAdaptive, Config.Value.parAdaptive);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parDuplicates, Config.Value.parDuplicates);

        return export(method, Config.Value.parGenRandom, typeExport, updatedProperties);
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport, Param.ParamsRandom properties) {

        return exportRandom(method, typeExport, properties.getParamMap());
    }

    public Iterable<String> exportRandom(String method, TypeExport typeExport) {

        return exportRandom(method, typeExport, new Param.ParamsRandom());
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Config.Value.parGenStatic, typeExport, updatedProperties);
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport, Param.ParamsStatic properties) {

        return exportStatic(method, typeExport, properties.getParamMap());
    }

    public Iterable<String> exportStatic(String method, TypeExport typeExport) {

        return exportStatic(method, typeExport, new Param.ParamsStatic());
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

        CollectionHelper.addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

        return generate(method, Config.Value.parGenNWise, updatedProperties);
    }

    public Iterable<Object[]> generateNWise(String method, Param.ParamsNWise properties) {

        return generateNWise(method, properties.getParamMap());
    }

    public Iterable<Object[]> generateNWise(String method) {

        return generateNWise(method, new Param.ParamsNWise());
    }

    public Iterable<Object[]> generatePairwise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        CollectionHelper.addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        CollectionHelper. addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

        return generate(method, Config.Value.parGenNWise, updatedProperties);
    }

    public Iterable<Object[]> generatePairwise(String method, Param.ParamsPairwise properties) {

        return generatePairwise(method, properties.getParamMap());
    }

    public Iterable<Object[]> generatePairwise(String method) {

        return generatePairwise(method, new Param.ParamsPairwise());
    }

    public Iterable<Object[]> generateCartesian(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, Config.Value.parGenCartesian, updatedProperties);
    }

    public Iterable<Object[]> generateCartesian(String method, Param.ParamsCartesian properties) {

        return generateCartesian(method, properties.getParamMap());
    }

    public Iterable<Object[]> generateCartesian(String method) {

        return generateCartesian(method, new Param.ParamsCartesian());
    }

    public Iterable<Object[]> generateRandom(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        CollectionHelper.addProperty(updatedProperties, Config.Key.parLength, Config.Value.parLength);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parAdaptive, Config.Value.parAdaptive);
        CollectionHelper.addProperty(updatedProperties, Config.Key.parDuplicates, Config.Value.parDuplicates);

        return generate(method, Config.Value.parGenRandom, updatedProperties);
    }

    public Iterable<Object[]> generateRandom(String method, Param.ParamsRandom properties) {

        return generateRandom(method, properties.getParamMap());
    }

    public Iterable<Object[]> generateRandom(String method) {

        return generateRandom(method, new Param.ParamsRandom());
    }

    public Iterable<Object[]> generateStatic(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, Config.Value.parGenStatic, updatedProperties);
    }

    public Iterable<Object[]> generateStatic(String method, Param.ParamsStatic properties) {

        return generateStatic(method, properties.getParamMap());
    }

    public Iterable<Object[]> generateStatic(String method) {

        return generateStatic(method, new Param.ParamsStatic());
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

}
