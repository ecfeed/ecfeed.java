package com.ecfeed.helper;

import com.ecfeed.*;
import com.ecfeed.data.ConnectionData;
import com.ecfeed.data.SessionData;
import com.ecfeed.parser.ChunkParser;
import com.ecfeed.parser.ChunkParserExport;
import com.ecfeed.parser.ChunkParserStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class ConnectionHelper {

    private ConnectionHelper() {
        throw new RuntimeException("The helper class cannot be instantiated");
    }

    public static InputStream getChunkStreamForHealthCheck(ConnectionData connectionData) {
        String request = generateURLForHealthCheck(connectionData.getHttpAddress());

        return getChunkStream(connectionData.getHttpClient(), request);
    }

    public static InputStream getChunkStreamForTestData(SessionData sessionData) {
        String request = generateURLForTestData(sessionData);

        return getChunkStream(sessionData.getHttpClient(), request);
    }

    private static String generateURLForHealthCheck(String generatorAddress) {

        return generatorAddress + "/" + Config.Key.urlHealthCheck;
    }

    private static String generateURLForTestData(SessionData sessionData) {
        Optional<TypeExport> template = sessionData.getTemplate();

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(sessionData.getGeneratorAddress()).append("/").append(Config.Key.urlService).append("?");

        if (template.isPresent() && !template.get().equals(TypeExport.Raw)) {
            requestBuilder.append(Config.Key.parRequestType).append("=").append(Config.Value.parRequestTypeExport);
        } else {
            requestBuilder.append(Config.Key.parRequestType).append("=").append(Config.Value.parRequestTypeStream);
        }

        requestBuilder.append("&").append(Config.Key.parClient).append("=").append(Config.Value.parClient);
        requestBuilder.append("&").append(Config.Key.parRequest).append("=");

        JSONObject request = new JSONObject();
        request.put(Config.Key.parModel, sessionData.getModel());
        request.put(Config.Key.parMethod, sessionData.getMethod());

        JSONObject userData = new JSONObject();
        userData.put(Config.Key.parDataSource, sessionData.getGeneratorType());
        userData.put(Config.Key.parProperties, sessionData.getProperties());

        request.put(Config.Key.parUserData, userData.toString().replaceAll("\"", "'"));

        if (template.isPresent() && !template.get().equals(TypeExport.Raw)) {
            request.put(Config.Key.parTemplate, template.get());
        }

        try {
            return  requestBuilder + URLEncoder.encode(request.toString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("The request could not be generated.");
        }

    }

    private static InputStream getChunkStream(HttpClient httpClient, String request) {

        try {
            HttpGet httpRequest = new HttpGet(request);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed (the generator address might be erroneous).", e);
        }
    }

    public static void validateConnection(ConnectionData connectionData) {
        IterableTestQueue<String> iterator = new IterableTestQueue<>(ChunkParserExport.create());

        try {
            processChunkStream(iterator, ConnectionHelper.getChunkStreamForHealthCheck(connectionData));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }

    public static ChunkParser<Optional<Object[]>> sendMockRequest(ConnectionData connectionData, String model, String method) {
        Map<String, Object> userProperties = new HashMap<>();
        CollectionHelper.addProperty(userProperties, Config.Key.parLength, "0");
        
        SessionData sessionData = SessionData.create(connectionData, model, method, Config.Value.parGenRandom);
        sessionData.setProperties(userProperties);

        ChunkParser<Optional<Object[]>> chunkParser = ChunkParserStream.create(sessionData);
        
        IterableTestQueue<Object[]> iterator = new IterableTestQueue<>(chunkParser);
        
        processChunkStream(iterator, ConnectionHelper.getChunkStreamForTestData(sessionData));
        dryChunkStream(iterator);

        return chunkParser;
    }

    public static void processChunkStream(IterableTestQueue<?> iterator, InputStream chunkInputStream) {
        String chunk;

        try (BufferedReader responseReader = new BufferedReader(new InputStreamReader(chunkInputStream))) {
            while ((chunk = responseReader.readLine()) != null) {
                processChunk(iterator, chunk);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was interrupted", e);
        }

        cleanup(iterator);
    }

    private static void processChunk(IterableTestQueue<?> iterator, String chunk) {

        iterator.append(chunk);
    }

    private static void cleanup(IterableTestQueue<?> iterator) {

        iterator.terminate();
    }

    private static void dryChunkStream(IterableTestQueue<?> iterator) {

        for (Object ignored : iterator) {
            nop(ignored);
        }
    }

    private static void nop(Object chunk) {

        System.out.println(chunk);
    }

}
