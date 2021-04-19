package com.ecfeed;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

public class TestProvider {

    private String model;
    private String generatorAddress;
    private String keyStorePassword;
    private Path keyStorePath;
    private HttpClient httpClient;
    private FeedbackData feedbackData;

    private TestProvider(String model, Map<String, String> config) {

        this.feedbackData = new FeedbackData();
        this.feedbackData.setModelId(model);

        setup(model, config);
    }

    public static TestProvider create(String modelUuid) {

        return new TestProvider(modelUuid, new HashMap<>());
    }

    public static TestProvider create(String model, Map<String, String> config) {

        return new TestProvider(model, config);
    }

    private void setup(String modelUuid, Map<String, String> config) {

        this.model = modelUuid;

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
            if (!keyStore.containsAlias(Config.Key.certClient)) {
                throw new IllegalArgumentException("The client certificate could not be found: " + keyStorePath.toAbsolutePath());
            }

            PrivateKeyStrategy strategy = (aliases, socket) -> Config.Key.certClient;
            return context.loadKeyMaterial(keyStore, keyStorePassword.toCharArray(), strategy);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IllegalArgumentException("The client certificate could not be accessed.", e);
        }
    }

    private SSLContextBuilder getTrustMaterial(SSLContextBuilder context, KeyStore keyStore) {

        try {
            if (!keyStore.containsAlias(Config.Key.certServer)) {
                throw new IllegalArgumentException("The server certificate could not be found: " + keyStorePath.toAbsolutePath());
            }

            Certificate cert = keyStore.getCertificate(Config.Key.certServer);
            TrustStrategy strategy = (chain, authType) -> Arrays.asList((Certificate[]) chain).contains(cert);
            return context.loadTrustMaterial(strategy);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalArgumentException("The server certificate could not be accessed.", e);
        }
    }

    public String getModel() {

        return model;
    }

    public String getGeneratorAddress() {

        return generatorAddress;
    }

    public Path getKeyStorePath() {

        return keyStorePath;
    }

    public Iterable<String> export(String method, String generator, TypeExport typeExport, Map<String, Object> properties) {
        Config.validateUserParameters(properties);

        IterableTestQueue<String> iterator = new IterableTestQueue<>(new ChunkParserExport());
        String userData = getUserData(generator, properties);

        new Thread(() -> {
            try {
                processChunkStream(iterator, getChunkStream(generateRequestURL(method, userData, Optional.of(typeExport.toString()))));
            } finally {
                iterator.terminate();
            }
        }).start();

        return iterator;
    }

    public Iterable<String> exportNWise(String method, TypeExport typeExport, Map<String, Object> properties) {

        this.feedbackData.setMethodInfo(method);
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

    private String getUserData(String generator, Map<String, Object> properties) {
        JSONObject userData = new JSONObject();

        transferProperty(Config.Key.parConstraints, userData, properties);
        transferProperty(Config.Key.parChoices, userData, properties);
        transferProperty(Config.Key.parTestSuites, userData, properties);

        userData.put(Config.Key.parDataSource, generator);
        userData.put(Config.Key.parProperties, properties);

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
        requestBuilder.append(this.generatorAddress).append("/").append(Config.Key.urlService).append("?");

        if (template.isPresent() && !template.get().equals(TypeExport.Raw.toString())) {
            requestBuilder.append(Config.Key.parRequestType).append("=").append(Config.Value.parRequestTypeExport);
        } else {
            requestBuilder.append(Config.Key.parRequestType).append("=").append(Config.Value.parRequestTypeStream);
        }

        requestBuilder.append("&").append(Config.Key.parClient).append("=").append(Config.Value.parClient);
        requestBuilder.append("&").append(Config.Key.parRequest).append("=");

        JSONObject request = new JSONObject(); // TODO - JSON example
        request.put(Config.Key.parModel, this.model);
        request.put(Config.Key.parMethod, method);
        request.put(Config.Key.parUserData, userData);

        if (template.isPresent() && !template.get().equals(TypeExport.Raw.toString())) {
            request.put(Config.Key.parTemplate, template.get());
        }

        String result = request.toString();

        log(result);

        try {
            result = URLEncoder.encode(result, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("The URL request could not be built");
        }


        return requestBuilder.toString() + result;
    }

    public void validateConnection() {
        IterableTestQueue<String> iterator = new IterableTestQueue<>(new ChunkParserExport());

        try {
            processChunkStream(iterator, getChunkStream(generateHealthCheckURL()));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }

    private String generateHealthCheckURL() {

        return this.generatorAddress + "/" + Config.Key.urlHealthCheck;
    }

    public List<String> getMethodNames(String methodName) {

        return Arrays.asList(sendMockRequest(methodName).getMethodNames());
    }

    public List<String> getMethodTypes(String methodName) {

        return Arrays.asList(sendMockRequest(methodName).getMethodTypes());
    }

    private ChunkParser<Optional<Object[]>> sendMockRequest(String methodName) {
        Map<String, Object> properties = new HashMap<>();
        addProperty(properties, Config.Key.parLength, "0");

        ChunkParser<Optional<Object[]>> chunkParser = new ChunkParserStream();
        IterableTestQueue<Object[]> iterator = new IterableTestQueue<>(chunkParser);

        String userData = getUserData(Config.Value.parGenRandom, properties);

        processChunkStream(iterator, getChunkStream(generateRequestURL(methodName, userData, Optional.empty())));

        dryChunkStream(iterator);

        return chunkParser;
    }

    public void sendFixedFeedback() {

        long testSessionNumber = System.currentTimeMillis();
        final String testSessionId = "testSession" + testSessionNumber;

        String requestText = createExampleRequestTest(testSessionId);

        sendFeedback(requestText);
    }

    public void sendFeedback(String requestText) {

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(this.generatorAddress).append("/").append(Config.Key.urlService).append("?");

        String url = this.generatorAddress + "/" + "streamFeedback"; // TODO - move to config

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(requestText));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            InputStream inputStream = httpResponse.getEntity().getContent(); // TODO
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed (the generator address might be erroneous): https://" + this.generatorAddress + "/", e);
        }
    }

    private String createExampleRequestTest(String testSessionId) {

        String testResults = getTestResulsAsText();

        String requestText =
                "{" +
                        "'testSessionId': '" + testSessionId + "', " +
                        "'modelId': 'TestUuid11', " +
                        "'methodInfo': 'test.Class1.testMethod(String arg1, String arg2)', " +
                        "'framework': 'Python', " +
                        "'timestamp': 1618401006, " +
                        "'generatorType': 'NWise', " +
                        "'generatorOptions': 'n=2, coverage=100', " +

                        "'testResults': " + testResults +

                        "}";

        requestText = requestText.replace("#", "\\\"");
        requestText = requestText.replace("'", "\"");

        return requestText;
    }

    private String getTestResulsAsText() {

        String testResults = "{ " +
                "'0:0': {'data': '{#testCase#:[{#name#:#choice11#,#value#:#V11#},{#name#:#choice21#,#value#:#V21#}]}', 'status': 'P', 'duration': 1394}, " +
                "'0:1': {'data': '{#testCase#:[{#name#:#choice12#,#value#:#V12#},{#name#:#choice21#,#value#:#V21#}]}', 'status': 'F', 'duration': 1513}, " +
                "'0:2': {'data': '{#testCase#:[{#name#:#choice12#,#value#:#V12#},{#name#:#choice22#,#value#:#V22#}]}', 'status': 'F', 'duration': 1513}, " +
                "'0:3': {'data': '{#testCase#:[{#name#:#choice11#,#value#:#V11#},{#name#:#choice22#,#value#:#V22#}]}', 'status': 'F', 'duration': 1513}" +
                "} ";
        return testResults;
    }

    private String getTestResultsAsJSONString() {

        String testResults = getTestResulsAsText();

        testResults = testResults.replace("#", "\\\"");
        testResults = testResults.replace("'", "\"");

        return testResults;
    }

    public void setFeedbackResult(String testId, String data, boolean isTestPass, long durationInMilliseconds, boolean hasNext) {

        FeedbackResult feedbackResult = new FeedbackResult();
        feedbackResult.setData(data);
        feedbackResult.setStatus(isTestPass);
        feedbackResult.setDuration(durationInMilliseconds);

        this.feedbackData.addFeedbackResult(testId, feedbackResult);

        if (hasNext) {
            return;
        }

        JSONObject jsonObject = this.feedbackData.createJsonObject();
        String feedbackText = jsonObject.toString();

//        String feedbackText = this.feedbackData.serialize();
//        String testResults = getTestResultsAsJSONString();
//        feedbackText = feedbackText.replace("\"#\"", testResults);

        sendFeedback(feedbackText);

//        System.out.println(feedbackText);
//        System.out.println("Sending feedback.");
//
//        sendFixedFeedback();
    }

    public void initializeFeedback(FeedbackData feedbackData) {

        // copy values without model id and method info

        this.feedbackData.setTestSessionId(feedbackData.getTestSessionId());
        this.feedbackData.setFramework(feedbackData.getFramework());
        this.feedbackData.setTimestamp(feedbackData.getTimestamp());
        this.feedbackData.setGeneratorType(feedbackData.getGeneratorType());
        this.feedbackData.setGeneratorOptions(feedbackData.getGeneratorOptions());
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

    private void log(String event) {

        System.out.println(event);
    }
}
