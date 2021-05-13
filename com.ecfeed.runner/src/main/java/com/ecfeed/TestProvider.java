package com.ecfeed;

import com.ecfeed.data.ConnectionData;
import com.ecfeed.data.SessionData;
import com.ecfeed.helper.ConnectionHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestProvider {

    private String model;
    private ConnectionData connectionData;

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
        this.connectionData = ConnectionData.create(
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

        return this.connectionData.getHttpAddress();
    }

    public Path getKeyStorePath() {

        return this.connectionData.getKeyStorePath();
    }

    public Iterable<String> export(String method, String generatorType, TypeExport typeExport, Map<String, Object> userProperties) {
        Config.validateUserParameters(userProperties);

        IterableTestQueue<String> iterator = new IterableTestQueue<>(new ChunkParserExport());

        SessionData sessionData = SessionData.create(this.connectionData, this.model);
        sessionData.updateRequestData(method, generatorType);
        sessionData.updateRequestProperties(userProperties);
        sessionData.updateRequestTemplate(typeExport);

        new Thread(() -> {
            try {
                processChunkStream(iterator, ConnectionHelper.getChunkStreamForTestData(sessionData));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

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

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

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

        addProperty(updatedProperties, Config.Key.parLength, Config.Value.parLength);
        addProperty(updatedProperties, Config.Key.parAdaptive, Config.Value.parAdaptive);
        addProperty(updatedProperties, Config.Key.parDuplicates, Config.Value.parDuplicates);

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

        IterableTestQueue<Object[]> iterator = new IterableTestQueue<>(new ChunkParserStream());

        SessionData sessionData = SessionData.create(this.connectionData, this.model);
        sessionData.updateRequestData(method, generator);
        sessionData.updateRequestProperties(properties);

        new Thread(() -> {
            try {
                processChunkStream(iterator, ConnectionHelper.getChunkStreamForTestData(sessionData));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    public Iterable<Object[]> generateNWise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

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

        addProperty(updatedProperties, Config.Key.parN, Config.Value.parN);
        addProperty(updatedProperties, Config.Key.parCoverage, Config.Value.parCoverage);

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

        addProperty(updatedProperties, Config.Key.parLength, Config.Value.parLength);
        addProperty(updatedProperties, Config.Key.parAdaptive, Config.Value.parAdaptive);
        addProperty(updatedProperties, Config.Key.parDuplicates, Config.Value.parDuplicates);

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

    private void addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }





    public void validateConnection() {
        IterableTestQueue<String> iterator = new IterableTestQueue<>(new ChunkParserExport());

        SessionData sessionData = SessionData.create(this.connectionData, this.model);

        try {
            processChunkStream(iterator, ConnectionHelper.getChunkStreamForHealthCheck(sessionData));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }



    public List<String> getMethodNames(String methodName) {

        return Arrays.asList(sendMockRequest(methodName).getMethodNames());
    }

    public List<String> getMethodTypes(String methodName) {

        return Arrays.asList(sendMockRequest(methodName).getMethodTypes());
    }

    private ChunkParser<Optional<Object[]>> sendMockRequest(String method) {
        Map<String, Object> userProperties = new HashMap<>();
        addProperty(userProperties, Config.Key.parLength, "0");

        ChunkParser<Optional<Object[]>> chunkParser = new ChunkParserStream();
        IterableTestQueue<Object[]> iterator = new IterableTestQueue<>(chunkParser);

        SessionData sessionData = SessionData.create(this.connectionData, this.model);
        sessionData.updateRequestData(method, Config.Value.parGenRandom);
        sessionData.updateRequestProperties(userProperties);

        processChunkStream(iterator, ConnectionHelper.getChunkStreamForTestData(sessionData));
        dryChunkStream(iterator);

        return chunkParser;
    }

    private void processChunkStream(IterableTestQueue<?> iterator, InputStream chunkInputStream) {
        String chunk;

        try(BufferedReader responseReader = new BufferedReader(new InputStreamReader(chunkInputStream))) {
            while((chunk = responseReader.readLine()) != null) {
                processChunk(iterator, chunk);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was interrupted", e);
        }

        cleanup(iterator);
    }

    private void processChunk(IterableTestQueue<?> iterator, String chunk) {

        iterator.append(chunk);
    }

    private void cleanup(IterableTestQueue<?> iterator) {

        iterator.terminate();
    }

    private void dryChunkStream(IterableTestQueue<?> iterator) {

        for (Object ignored : iterator) {
            nop(ignored);
        }
    }

    private void nop(Object chunk) {

        System.out.println(chunk);
    }

}
