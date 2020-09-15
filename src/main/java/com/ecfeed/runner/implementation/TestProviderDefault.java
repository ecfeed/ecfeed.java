package com.ecfeed.runner.implementation;

import com.ecfeed.runner.Config;
import com.ecfeed.runner.constant.Template;
import com.ecfeed.runner.design.IteratorTestStream;
import com.ecfeed.runner.design.TestProvider;

import com.ecfeed.runner.implementation.parser.export.ExportChunkParser;
import com.ecfeed.runner.implementation.parser.stream.StreamChunkParser;
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

public class TestProviderDefault implements TestProvider {

    private String model;
    private String generatorAddress;
    private String keyStorePassword;
    private Path keyStorePath;
    private HttpClient httpClient;

    private TestProviderDefault(String model, Map<String, String> config) {

        setup(model, config);
    }

    public static TestProvider getTestProvider(String model) {

        return new TestProviderDefault(model, new HashMap<>());
    }

    public static TestProvider getTestProvider(String model, Map<String, String> config) {

        return new TestProviderDefault(model, config);
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
            } catch (IllegalArgumentException e) {
                continue;
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
            PrivateKeyStrategy strategy = (aliases, socket) -> Config.Name.certClient;
            return context.loadKeyMaterial(keyStore, keyStorePassword.toCharArray(), strategy);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IllegalArgumentException("The client certificate could not be accessed.", e);
        }
    }

    private SSLContextBuilder getTrustMaterial(SSLContextBuilder context, KeyStore keyStore) {

        try {
            Certificate cert = keyStore.getCertificate(Config.Name.certServer);
            TrustStrategy strategy = (chain, authType) -> Arrays.asList(chain).stream().anyMatch(e -> e.equals(cert));
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
    public String getKeyStorePassword() {

        return keyStorePassword;
    }

    @Override
    public Path getKeyStorePath() {

        return keyStorePath;
    }

    @Override
    public IteratorTestStream<String> export(String method, String generator, Template template, Map<String, Object> properties) {
        IteratorTestStream<String> iterator = new IteratorTestStreamDefault<>(new ExportChunkParser());
        String userData = getUserData(generator, properties);

        sendRequest(iterator, getRequest(method, userData, Optional.of(template.toString())));

        return iterator;
    }

    @Override
    public IteratorTestStream<String> exportNWise(String method, Template template, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Name.parN, Config.Value.parN);
        addProperty(updatedProperties, Config.Name.parCoverage, Config.Value.parCoverage);

        return export(method, Config.Value.parGenNWise, template, updatedProperties);
    }

    @Override
    public IteratorTestStream<String> exportCartesian(String method, Template template, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Config.Value.parGenCartesian, template, updatedProperties);
    }

    @Override
    public IteratorTestStream<String> exportRandom(String method, Template template, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Name.parLength, Config.Value.parLength);
        addProperty(updatedProperties, Config.Name.parAdaptive, Config.Value.parAdaptive);
        addProperty(updatedProperties, Config.Name.parDuplicates, Config.Value.parDuplicates);

        return export(method, Config.Value.parGenRandom, template, updatedProperties);
    }

    @Override
    public IteratorTestStream<String> exportStatic(String method, Template template, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, Config.Value.parGenStatic, template, updatedProperties);
    }

    @Override
    public IteratorTestStream<Object[]> stream(String method, String generator, Map<String, Object> properties) {
        IteratorTestStream<Object[]> iterator = new IteratorTestStreamDefault<>(new StreamChunkParser());
        String userData = getUserData(generator, properties);

        sendRequest(iterator, getRequest(method, userData, Optional.empty()));

        return iterator;
    }

    @Override
    public IteratorTestStream<Object[]> streamNWise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Name.parN, Config.Value.parN);
        addProperty(updatedProperties, Config.Name.parCoverage, Config.Value.parCoverage);

        return stream(method, Config.Value.parGenNWise, updatedProperties);
    }

    @Override
    public IteratorTestStream<Object[]> streamCartesian(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return stream(method, Config.Value.parGenCartesian, updatedProperties);
    }

    @Override
    public IteratorTestStream<Object[]> streamRandom(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, Config.Name.parLength, Config.Value.parLength);
        addProperty(updatedProperties, Config.Name.parAdaptive, Config.Value.parAdaptive);
        addProperty(updatedProperties, Config.Name.parDuplicates, Config.Value.parDuplicates);

        return stream(method, Config.Value.parGenRandom, updatedProperties);
    }

    @Override
    public IteratorTestStream<Object[]> streamStatic(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return stream(method, Config.Value.parGenStatic, updatedProperties);
    }

    private Map<String, Object> addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }

        return map;
    }

    private String getUserData(String generator, Map<String, Object> properties) {
        JSONObject userData = new JSONObject();

        if (properties.containsKey(Config.Name.parConstraints)) {
            userData.put(Config.Name.parConstraints, properties.get(Config.Name.parConstraints));
            properties.remove(Config.Name.parConstraints);
        }

        if (properties.containsKey(Config.Name.parChoices)) {
            userData.put(Config.Name.parChoices, properties.get(Config.Name.parChoices));
            properties.remove(Config.Name.parChoices);
        }

        if (properties.containsKey(Config.Name.parTestSuites)) {
            userData.put(Config.Name.parTestSuites, properties.get(Config.Name.parTestSuites));
            properties.remove(Config.Name.parTestSuites);
        }

        userData.put(Config.Name.parDataSource, generator);
        userData.put(Config.Name.parProperties, properties);

        return userData.toString().replaceAll("\"", "'");
    }

    private String getRequest(String method, String userData, Optional<String> template) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(this.generatorAddress + "/" + Config.Name.urlService + "?");

        if (template.isPresent()) {
            requestBuilder.append(Config.Name.parRequestType + "=" + Config.Value.parRequestTypeExport);
        } else {
            requestBuilder.append(Config.Name.parRequestType + "=" + Config.Value.parRequestTypeStream);
        }

        requestBuilder.append("&" + Config.Name.parClient + "=" + Config.Value.parClient);
        requestBuilder.append("&" + Config.Name.parRequest + "=");

        JSONObject request = new JSONObject();
        request.put(Config.Name.parModel, this.model);
        request.put(Config.Name.parMethod, method);
        request.put(Config.Name.parUserData, userData);

        if (template.isPresent()) {
            request.put(Config.Name.parTemplate, template.get());
        }

        String result = request.toString();
        System.out.println(result);
        try {
            result = URLEncoder.encode(result, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("The URL request could not be built");
        }


        return requestBuilder.toString() + result;
    }

    @Override
    public void validateConnection() {

    }

    @Override
    public List<String> getMethodNames(String methodName) {
        return null;
    }

    @Override
    public List<String> getMethodTypes(String methodName) {
        return null;
    }

    @Override
    public void sendRequest(IteratorTestStream iterator, String request) {

        try {
            HttpGet httpRequest = new HttpGet(request);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            processChunkStream(iterator, httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed", e);
        }

        cleanup(iterator);
    }

    private void processChunkStream(IteratorTestStream iterator, InputStream chunkInputStream) {
        String chunk;

        try(BufferedReader responseReader = new BufferedReader(new InputStreamReader(chunkInputStream))) {
            while((chunk = responseReader.readLine()) != null) {
                processChunk(iterator, chunk);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was interrupted", e);
        }
    }

    private void processChunk(IteratorTestStream iterator, String chunk) {

        iterator.append(chunk);
    }

    private void cleanup(IteratorTestStream iterator) {

        iterator.terminate();
    }
}
