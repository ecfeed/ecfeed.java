package com.ecfeed.connection;

import com.ecfeed.Factory;
import com.ecfeed.config.ConfigDefault;
import com.ecfeed.session.dto.DataSession;
import com.ecfeed.session.dto.DataSessionConnection;
import com.ecfeed.queue.IterableTestQueue;
import com.ecfeed.system.SystemHandler;
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

public class ConnectionHandlerDefault implements ConnectionHandler {

    private final SystemHandler systemHandler = Factory.getSystemHandler();

    private ConnectionHandlerDefault() {
    }

    public static ConnectionHandler create() {

        return new ConnectionHandlerDefault();
    }

    @Override
    public InputStream getChunkStreamForTestData(DataSession dataSession) {
        var dataSessionFacade = Factory.getDataSessionFacade(dataSession);

        return createChunkStreamGet(dataSession.getHttpClient(), dataSessionFacade.generateURLForTestData());
    }

    @Override
    public InputStream getFeedbackRequest(DataSession dataSession) {
        var dataSessionFacade = Factory.getDataSessionFacade(dataSession);

        final int MAX_ATTEMPTS = 5;
        final boolean IS_DIAGNOSTICS = false;

        systemHandler.printDiagnosticMessage("Sending feedback...", IS_DIAGNOSTICS);

        for (int attempt_number = 1; attempt_number <= MAX_ATTEMPTS; attempt_number++) {

            try {
                return sendPostRequest(
                        dataSession.getHttpClient(),
                        dataSessionFacade.generateURLForFeedback(),
                        dataSessionFacade.generateBodyForFeedback());

            } catch (Exception e) {

                String attemptFailed = "Sending feedback failed at attempt: " + attempt_number;
                systemHandler.printDiagnosticMessage(attemptFailed, IS_DIAGNOSTICS);

                if (attempt_number >=  MAX_ATTEMPTS) {
                    throw new RuntimeException("Sending feedback failed!", e);
                }

                systemHandler.sleep(500);
            }
        }

        return null;
    }

    @Override
    public void validateConnection(DataSessionConnection connection) {
        var iterator = Factory.getIterableTestQueueExport();

        try {
            processChunkStream(iterator, getChunkStreamForHealthCheck(connection));
            dryChunkStream(iterator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The connection could not be established", e);
        }
    }

    @Override
    public DataSession sendMockRequest(DataSessionConnection connection, String model, String method) {
        Map<String, Object> userProperties = new HashMap<>();

        userProperties.put(ConfigDefault.Key.parLength, "0");

        DataSession dataSession = Factory.getDataSession(connection, model, method, TypeGenerator.Random);
        dataSession.setOptionsGenerator(userProperties);

        var iterator = Factory.getIterableTestQueueStream(dataSession);

        processChunkStream(iterator, getChunkStreamForTestData(dataSession));
        dryChunkStream(iterator);

        return dataSession;
    }

    @Override
    public void processChunkStream(IterableTestQueue<?> iterator, InputStream chunkInputStream) {
        String chunk;

        try (BufferedReader responseReader = new BufferedReader(new InputStreamReader(chunkInputStream))) {
            while ((chunk = responseReader.readLine()) != null) {
                processChunk(iterator, chunk);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was interrupted!", e);
        }

        cleanup(iterator);
    }

    private InputStream getChunkStreamForHealthCheck(DataSessionConnection connection) {
        String request = generateURLForHealthCheck(connection.getHttpAddress());

        return createChunkStreamGet(connection.getHttpClient(), request);
    }

    private String generateURLForHealthCheck(String generatorAddress) {

        return generatorAddress + "/" + ConfigDefault.Key.urlHealthCheck;
    }

    private InputStream createChunkStreamGet(HttpClient httpClient, String request) {

        try {
            HttpGet httpGet = new HttpGet(request);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new IllegalArgumentException("The connection was closed (the generator address might be erroneous)!", e);
        }
    }

    private InputStream sendPostRequest(HttpClient httpClient, String uri, String body) {

        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(body));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return httpResponse.getEntity().getContent();
        } catch (IOException e) {
            throw new RuntimeException("Sending post request failed!", e);
        }
    }

    private void processChunk(IterableTestQueue<?> iterator, String chunk) {

        iterator.append(chunk);
    }

    private void cleanup(IterableTestQueue<?> iterator) {

        iterator.terminate();
    }

    private void dryChunkStream(IterableTestQueue<?> iterator) {

        for (Object ignored : iterator) {
            systemHandler.nop(ignored);
        }
    }
}
