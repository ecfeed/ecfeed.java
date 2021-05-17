package com.ecfeed.helper;

import com.ecfeed.Config;
import com.ecfeed.IterableTestQueue;
import com.ecfeed.data.ConnectionData;
import com.ecfeed.data.SessionData;
import com.ecfeed.parser.ChunkParser;
import com.ecfeed.parser.ChunkParserExport;
import com.ecfeed.parser.ChunkParserStream;
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
import java.util.Optional;

public final class ConnectionHelper {

    private ConnectionHelper() {

        throw new RuntimeException("The helper class cannot be instantiated");
    }

    public static InputStream getChunkStreamForHealthCheck(ConnectionData connection) {
        String request = generateURLForHealthCheck(connection.getHttpAddress());

        return createChunkStreamGet(connection.getHttpClient(), request);
    }

    public static InputStream getChunkStreamForTestData(SessionData sessionData) {

        return createChunkStreamGet(sessionData.getHttpClient(), sessionData.generateURLForTestData());
    }

    public static InputStream getChunkStreamForFeedback(SessionData sessionData) {

        return createChunkStreamPost(sessionData.getHttpClient(), sessionData.generateURLForFeedback(), sessionData.generateBodyForFeedback());
    }

    private static String generateURLForHealthCheck(String generatorAddress) {

        return generatorAddress + "/" + Config.Key.urlHealthCheck;
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

    public static void validateConnection(ConnectionData connection) {
        IterableTestQueue<String> iterator = new IterableTestQueue<>(ChunkParserExport.create());

        try {
            processChunkStream(iterator, ConnectionHelper.getChunkStreamForHealthCheck(connection));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }

    public static ChunkParser<Optional<Object[]>> sendMockRequest(ConnectionData connection, String model, String method) {
        Map<String, Object> userProperties = new HashMap<>();

        userProperties.put(Config.Key.parLength, "0");

        SessionData sessionData = SessionData.create(connection, model, method, Config.Value.parGenRandom);
        sessionData.setGeneratorOptions(userProperties);

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