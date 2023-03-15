package com.ecfeed.helper;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.data.DataConnection;
import com.ecfeed.data.DataHelper;
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

        return createChunkStreamGet(dataSession.getHttpClient(), DataHelper.generateURLForTestData(dataSession));
    }

    public static InputStream sendFeedbackRequest(DataSession dataSession) {

        final int MAX_ATTEMPTS = 5;
        final boolean IS_DIAGNOSTICS = false;

        printDiagnosticMessage("Sending feeddback...", IS_DIAGNOSTICS);

        for (int attempt_number = 1; attempt_number <= MAX_ATTEMPTS; attempt_number++) {

            try {
                return sendPostRequest(
                        dataSession.getHttpClient(),
                        DataHelper.generateURLForFeedback(dataSession),
                        DataHelper.generateBodyForFeedback(dataSession));

            } catch (Exception e) {

                String attemptFailed = "Sending feeddback failed at attempt: " + attempt_number;
                printDiagnosticMessage(attemptFailed, IS_DIAGNOSTICS);

                if (attempt_number >=  MAX_ATTEMPTS) {
                    throw new RuntimeException("Sending feedback failed.", e);
                }

                sleep(500);
            }
        }

        return null;
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
        dataSession.setOptionsGenerator(userProperties);

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

    private static void printDiagnosticMessage(String message, boolean isDiagnostic) {

        if (isDiagnostic) {
            System.out.println(message);
        }
    }

    static void sleep(int milliseconds) { // TODO move to helper

        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
        }
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

    private static InputStream sendPostRequest(HttpClient httpClient, String uri, String body) {

        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(body));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new RuntimeException("Sending post request failed.", e);
        }
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
