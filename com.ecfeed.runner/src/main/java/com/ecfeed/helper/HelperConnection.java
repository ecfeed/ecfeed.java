package com.ecfeed.helper;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.data.DataConnection;
import com.ecfeed.data.DataSession;
import com.ecfeed.queue.IterableTestQueue;
import com.ecfeed.type.TypeGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class HelperConnection {

    private HelperConnection() {

        throw new RuntimeException("The helper class cannot be instantiated");
    }

    public static InputStream getChunkStreamForHealthCheck(DataConnection connection) {
        String request = generateURLForHealthCheck(connection.getHttpAddress());

        return createChunkStreamGet(connection.getHttpClient(), request);
    }

    public static InputStream getChunkStreamForTestData(DataSession dataSession) {

        return createChunkStreamGet(dataSession.getHttpClient(), dataSession.generateURLForTestData());
    }

    public static InputStream getChunkStreamForFeedback(DataSession dataSession) {

        return createChunkStreamPost(dataSession.getHttpClient(), dataSession.generateURLForFeedback(), dataSession.generateBodyForFeedback());
    }

    private static String generateURLForHealthCheck(String generatorAddress) {

        return generatorAddress + "/" + ConfigDefault.Key.urlHealthCheck;
    }

    private static InputStream createChunkStreamGet(HttpClient httpClient, String request) {

        try {
            HttpGet httpGet = new HttpGet(request);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed (the generator address might be erroneous).", e);
        }
    }

    private static InputStream createChunkStreamPost(HttpClient httpClient, String request, String body) {

        try {
            HttpPost httpPost = new HttpPost(request);
            httpPost.setEntity(new StringEntity(body));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed (the generator address might be erroneous).", e);
        }
    }

    public static void validateConnection(DataConnection connection) {
        IterableTestQueue<String> iterator = IterableTestQueue.createForExport();

        try {
            processChunkStream(iterator, HelperConnection.getChunkStreamForHealthCheck(connection));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }

    public static DataSession sendMockRequest(DataConnection connection, String model, String method) {
        Map<String, Object> userProperties = new HashMap<>();

        userProperties.put(ConfigDefault.Key.parLength, "0");

        DataSession dataSession = DataSession.create(connection, model, method, TypeGenerator.Random);
        dataSession.setGeneratorOptions(userProperties);

        IterableTestQueue<Object[]> iterator = IterableTestQueue.createForStream(dataSession);
        
        processChunkStream(iterator, HelperConnection.getChunkStreamForTestData(dataSession));
        dryChunkStream(iterator);

        return dataSession;
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
