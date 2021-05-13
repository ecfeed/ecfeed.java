package com.ecfeed.helper;

import com.ecfeed.ChunkParserStream;
import com.ecfeed.Config;
import com.ecfeed.IterableTestQueue;
import com.ecfeed.TypeExport;
import com.ecfeed.dto.SessionData;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class RequestHelper {

    private RequestHelper() {
        throw new RuntimeException("The helper class cannot be instantiated");
    }

    public static String generateURLForHealthCheck(SessionData sessionData) {

        return sessionData.getGeneratorAddress() + "/" + Config.Key.urlHealthCheck;
    }

    public static String generateURLForTestData(SessionData sessionData) {
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
        userData.put(Config.Key.parProperties, sessionData.getGeneratorOptions());

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

    public static InputStream getChunkStreamForHealthCheck(SessionData sessionData) {
        String request = RequestHelper.generateURLForHealthCheck(sessionData);

        return RequestHelper.getChunkStream(sessionData.getHttpClient(), request);
    }

    public static InputStream getChunkStreamForTestData(SessionData sessionData) {
        String request = RequestHelper.generateURLForTestData(sessionData);

        return RequestHelper.getChunkStream(sessionData.getHttpClient(), request);
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
}
