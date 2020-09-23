package com.ecfeed.implementation;

import com.ecfeed.Configuration;
import com.ecfeed.constant.ExportTemplate;
import com.ecfeed.design.IterableTestStream;
import com.ecfeed.design.TestProvider;

import com.ecfeed.design.ChunkParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;

public class DefaultTestProvider implements TestProvider {

    private static final boolean development = true;

    private String model;
    private String generatorAddress;
    private String keyStorePassword;
    private Path keyStorePath;
    private HttpClient httpClient;

    private DefaultTestProvider(String model, Map<String, String> config) {

        setup(model, config);
    }

    public static TestProvider getTestProvider(String model) {

        return new DefaultTestProvider(model, new HashMap<>());
    }

    public static TestProvider getTestProvider(String model, Map<String, String> config) {

        return new DefaultTestProvider(model, config);
    }

    private void setup(String model, Map<String, String> config) {

        this.model = model;

        this.generatorAddress = setupExtractGeneratorAddress(config);
        this.keyStorePassword = setupExtractKeyStorePassword(config);
        this.keyStorePath = setupExtractKeyStorePath(config);
        this.httpClient = setupGetHTTPClient(getKeyStoreInstance(this.keyStorePath));
    }

    private String setupExtractGeneratorAddress(Map<String, String> config) {
        String value = config.get("generatorAddress");

        return value != null ? value : Configuration.Value.generatorAddress;
    }

    private String setupExtractKeyStorePassword(Map<String, String> config) {
        String value = config.get("keyStorePassword");

        return value != null ? value : Configuration.Value.keyStorePassword;
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

        for (String address : Configuration.Value.keyStorePath) {
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

    private KeyStore getKeyStoreInstance(Path path) {

        try (InputStream keyStoreInputStream = Files.newInputStream(path)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
            return keyStore;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("The algorithm for checking the keystore integrity could not be found.", e);
        } catch (CertificateException e) {
            throw new IllegalArgumentException("At least one of the certificates included in the keystore could not be loaded.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("The keystore password is incorrect. Store path: " + path, e);
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException("The keystore could not be accessed.", e);
        }
    }

    private HttpClient setupGetHTTPClient(KeyStore keyStore) {

        return HttpClients.custom().setSSLContext(getSSLContext(keyStore)).build();
    }

    private SSLContext getSSLContext(KeyStore keyStore) {

        try {
            return getKeyMaterial(getTrustMaterial(SSLContexts.custom(), keyStore), keyStore).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalArgumentException("KeyStore certificates could not be loaded.", e);
        }
    }

    private SSLContextBuilder getKeyMaterial(SSLContextBuilder context, KeyStore keyStore) {

        try {
            if (!keyStore.containsAlias(Configuration.Name.certClient)) {
                throw new IllegalArgumentException("The client certificate could not be found: " + keyStorePath.toAbsolutePath());
            }

            PrivateKeyStrategy strategy = (aliases, socket) -> Configuration.Name.certClient;
            return context.loadKeyMaterial(keyStore, keyStorePassword.toCharArray(), strategy);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IllegalArgumentException("The client certificate could not be accessed.", e);
        }
    }

    private SSLContextBuilder getTrustMaterial(SSLContextBuilder context, KeyStore keyStore) {

        try {
            if (!keyStore.containsAlias(Configuration.Name.certServer)) {
                throw new IllegalArgumentException("The server certificate could not be found: " + keyStorePath.toAbsolutePath());
            }

            Certificate cert = keyStore.getCertificate(Configuration.Name.certServer);
            TrustStrategy strategy = (chain, authType) -> Arrays.asList(chain).contains(cert);
            return context.loadTrustMaterial(strategy);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalArgumentException("The server certificate could not be accessed.", e);
        }
    }

    @Override
    public String getModel() {

        return model;
    }

    @Override
    public String getGeneratorAddress() {

        return generatorAddress;
    }

    @Override
    public Path getKeyStorePath() {

        return keyStorePath;
    }

    @Override
    public Iterable<String> export(String method, String generator, ExportTemplate exportTemplate, Map<String, Object> properties) {
        IterableTestStream<String> iterator = new DefaultIterableTestStream<>(new ExportChunkParser());
        String userData = getUserData(generator, properties);

        new Thread(() -> {
            try {
                processChunkStream(iterator, getChunkStream(generateRequestURL(method, userData, Optional.of(exportTemplate.toString()))));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    @Override
    public Iterable<String> exportNWise(String method, ExportTemplate exportTemplate, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Configuration.Name.parN, Configuration.Value.parN);
        addProperty(updatedProperties, Configuration.Name.parCoverage, Configuration.Value.parCoverage);

        return export(method, Configuration.Value.parGenNWise, exportTemplate, updatedProperties);
    }

    @Override
    public Iterable<String> exportCartesian(String method, ExportTemplate exportTemplate, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Configuration.Value.parGenCartesian, exportTemplate, updatedProperties);
    }

    @Override
    public Iterable<String> exportRandom(String method, ExportTemplate exportTemplate, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Configuration.Name.parLength, Configuration.Value.parLength);
        addProperty(updatedProperties, Configuration.Name.parAdaptive, Configuration.Value.parAdaptive);
        addProperty(updatedProperties, Configuration.Name.parDuplicates, Configuration.Value.parDuplicates);

        return export(method, Configuration.Value.parGenRandom, exportTemplate, updatedProperties);
    }

    @Override
    public Iterable<String> exportStatic(String method, ExportTemplate exportTemplate, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Configuration.Value.parGenStatic, exportTemplate, updatedProperties);
    }

    @Override
    public Iterable<Object[]> generate(String method, String generator, Map<String, Object> properties) {
        IterableTestStream<Object[]> iterator = new DefaultIterableTestStream<>(new StreamChunkParser());
        String userData = getUserData(generator, properties);

        new Thread(() -> {
            try {
                processChunkStream(iterator, getChunkStream(generateRequestURL(method, userData, Optional.empty())));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    @Override
    public Iterable<Object[]> generateNWise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Configuration.Name.parN, Configuration.Value.parN);
        addProperty(updatedProperties, Configuration.Name.parCoverage, Configuration.Value.parCoverage);

        return generate(method, Configuration.Value.parGenNWise, updatedProperties);
    }

    @Override
    public Iterable<Object[]> generateCartesian(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, Configuration.Value.parGenCartesian, updatedProperties);
    }

    @Override
    public Iterable<Object[]> generateRandom(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Configuration.Name.parLength, Configuration.Value.parLength);
        addProperty(updatedProperties, Configuration.Name.parAdaptive, Configuration.Value.parAdaptive);
        addProperty(updatedProperties, Configuration.Name.parDuplicates, Configuration.Value.parDuplicates);

        return generate(method, Configuration.Value.parGenRandom, updatedProperties);
    }

    @Override
    public Iterable<Object[]> generateStatic(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, Configuration.Value.parGenStatic, updatedProperties);
    }

    private Map<String, Object> addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }

        return map;
    }

    private String getUserData(String generator, Map<String, Object> properties) {
        JSONObject userData = new JSONObject();

        transferProperty(Configuration.Name.parConstraints, userData, properties);
        transferProperty(Configuration.Name.parChoices, userData, properties);
        transferProperty(Configuration.Name.parTestSuites, userData, properties);

        userData.put(Configuration.Name.parDataSource, generator);
        userData.put(Configuration.Name.parProperties, properties);

        return userData.toString().replaceAll("\"", "'");
    }

    private void transferProperty(String propertyName, JSONObject userData, Map<String, Object> properties) {

        if (properties.containsKey(propertyName)) {
            userData.put(propertyName, properties.get(propertyName));
            properties.remove(propertyName);
        }
    }

    private String generateRequestURL(String method, String userData, Optional<String> template) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(this.generatorAddress).append("/").append(Configuration.Name.urlService).append("?");

        if (template.isPresent()) {
            requestBuilder.append(Configuration.Name.parRequestType).append("=").append(Configuration.Value.parRequestTypeExport);
        } else {
            requestBuilder.append(Configuration.Name.parRequestType).append("=").append(Configuration.Value.parRequestTypeStream);
        }

        requestBuilder.append("&").append(Configuration.Name.parClient).append("=").append(Configuration.Value.parClient);
        requestBuilder.append("&").append(Configuration.Name.parRequest).append("=");

        JSONObject request = new JSONObject();
        request.put(Configuration.Name.parModel, this.model);
        request.put(Configuration.Name.parMethod, method);
        request.put(Configuration.Name.parUserData, userData);

        template.ifPresent(s -> request.put(Configuration.Name.parTemplate, s));

        String result = request.toString();

        log(result);

        try {
            result = URLEncoder.encode(result, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("The URL request could not be built");
        }


        return requestBuilder.toString() + result;
    }

    @Override
    public void validateConnection() {
        IterableTestStream<String> iterator = new DefaultIterableTestStream<>(new ExportChunkParser());

        try {
            processChunkStream(iterator, getChunkStream(generateHealthCheckURL()));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }

    private String generateHealthCheckURL() {

        return this.generatorAddress + "/" + Configuration.Name.urlHealthCheck;
    }

    @Override
    public List<String> getMethodNames(String methodName) {

        return Arrays.asList(sendMockRequest(methodName).getMethodNames());
    }

    @Override
    public List<String> getMethodTypes(String methodName) {

        return Arrays.asList(sendMockRequest(methodName).getMethodTypes());
    }

    private ChunkParser sendMockRequest(String methodName) {
        Map<String, Object> properties = new HashMap<>();
        addProperty(properties, Configuration.Name.parLength, "0");

        ChunkParser chunkParser = new StreamChunkParser();
        IterableTestStream<Object[]> iterator = new DefaultIterableTestStream<Object[]>(chunkParser);

        String userData = getUserData(Configuration.Value.parGenRandom, properties);

        processChunkStream(iterator, getChunkStream(generateRequestURL(methodName, userData, Optional.empty())));

        dryChunkStream(iterator);

        return chunkParser;
    }

    private InputStream getChunkStream(String request) {

        try {
            HttpGet httpRequest = new HttpGet(request);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed (the generator address might be erroneous): https://" + this.generatorAddress + "/", e);
        }
    }

    private void processChunkStream(IterableTestStream<?> iterator, InputStream chunkInputStream) {
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

    private void processChunk(IterableTestStream<?> iterator, String chunk) {

        iterator.append(chunk);
    }

    private void cleanup(IterableTestStream<?> iterator) {

        iterator.terminate();
    }

    private void dryChunkStream(IterableTestStream<?> iterator) {

        for (Object ignored : iterator) {
            nop(ignored);
        }
    }

    private void nop(Object chunk) {

        System.out.println(chunk);
    }

    private void log(String event) {

        if (development) {
            System.out.println(event);
        }
    }
}
