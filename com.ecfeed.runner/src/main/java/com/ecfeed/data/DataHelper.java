package com.ecfeed.data;

import com.ecfeed.TestHandle;
import com.ecfeed.config.ConfigDefault;
import com.ecfeed.helper.HelperConnection;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class DataHelper {

    public static String generateURLForTestData(DataSession data) {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForTestDataCore(data, requestBuilder);
        generateURLForTestDataParameters(data, requestBuilder);

        return requestBuilder.toString();
    }

    private static void generateURLForTestDataCore(DataSession data, StringBuilder builder) {

        builder.append(getHttpAddress(data)).append("/").append(ConfigDefault.Key.urlService);
    }

    private static void generateURLForTestDataParameters(DataSession data, StringBuilder builder) {
        String type = data.getExportTemplate().isPresent() ? ConfigDefault.Value.parRequestTypeExport : ConfigDefault.Value.parRequestTypeStream;

        builder.append("?");
        builder.append(ConfigDefault.Key.reqDataRequestType).append("=").append(type);
        builder.append("&");
        builder.append(ConfigDefault.Key.reqDataClient).append("=").append(ConfigDefault.Value.parClient);
        builder.append("&");
        builder.append(ConfigDefault.Key.reqDataRequest).append("=").append(generateURLForTestDataRequest(data));
    }

    public static String generateURLForFeedback(DataSession data) {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForFeedbackCore(data, requestBuilder);
        generateURLForFeedbackParameters(data, requestBuilder);

        return requestBuilder.toString();
    }

    private static void generateURLForFeedbackCore(DataSession data, StringBuilder builder) {

        builder.append(getHttpAddress(data)).append("/").append(ConfigDefault.Key.urlFeedback);
    }

    private static void generateURLForFeedbackParameters(DataSession data, StringBuilder builder) { }

    public static String generateURLForTestDataRequest(DataSession data) {
        JSONObject request = new JSONObject();

        request.put(ConfigDefault.Key.reqDataMode, data.getModel());
        request.put(ConfigDefault.Key.reqDataMethod, data.getMethodName());
        request.put(ConfigDefault.Key.reqDataUserData, generateURLForTestDataRequestUserData(data));

        data.getExportTemplate().ifPresent(e -> request.put(ConfigDefault.Key.parDataTemplate, e));

        try {
            return URLEncoder.encode(request.toString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The request could not be generated.");
        }
    }

    private static String generateURLForTestDataRequestUserData(DataSession data) {
        JSONObject requestUserData = new JSONObject();

        requestUserData.put(ConfigDefault.Key.parDataSource, data.getGenerator().getName());
        requestUserData.put(ConfigDefault.Key.parProperties, data.getOptionsGenerator());

        if (data.getConstraints() != null) {
            requestUserData.put(ConfigDefault.Key.parConstraints, data.getConstraints());
        }
        if (data.getChoices() != null) {
            requestUserData.put(ConfigDefault.Key.parChoices, data.getChoices());
        }
        if (data.getTestSuites() != null) {
            requestUserData.put(ConfigDefault.Key.parTestSuites, data.getTestSuites());
        }

        return requestUserData.toString().replaceAll("\"", "'");
    }

    private static String getHttpAddress(DataSession data) {
        String httpAddress = data.getConnection().getHttpAddress();

        if (httpAddress == null) {
            throw new IllegalArgumentException("The generator address is not defined");
        }

        if (!httpAddress.startsWith("https://")) {
            throw new IllegalArgumentException("The generator address should start with https://");
        }

        return httpAddress;
    }

    public static String generateBodyForFeedback(DataSession data) {
        JSONObject json = new JSONObject();

        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackModel, data.getModel());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackMethod, data.getMethodNameQualified());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestSessionId, data.getTestSessionId());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestSessionLabel, data.getTestSessionLabel());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackFramework, ConfigDefault.Value.parClient);
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTimestamp, data.getTimestamp());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackCustom, data.getOptionsCustom());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestSuites, data.getTestSuites());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackConstraints, data.getConstraints());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackChoices, data.getChoices());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackTestResults, data.getTestResults());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackGeneratorType, data.getGenerator().getNickname());
        parseFeedbackElement(json, ConfigDefault.Key.reqFeedbackGeneratorOptions, data.getOptionsGenerator().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ")));

        return json.toString();
    }

    public static String generateBodyForTestData(DataSession data) {

        return "";
    }

    private static void parseFeedbackElement(JSONObject json, String key, Object value) {

        if (value == null) {
            return;
        }

        if (value instanceof String) {
            if (value.toString().equalsIgnoreCase("") || value.toString().equalsIgnoreCase(ConfigDefault.Value.parAll)) {
                return;
            }
        }

        if (value instanceof Map<?,?>) {
            if (((Map<?, ?>) value).size() == 0) {
                return;
            }
        }

        if (value instanceof Collection<?>) {
            if (((Collection<?>) value).size() == 0) {
                return;
            }
        }

        json.put(key, value);
    }

    public static void feedbackSetComplete(DataSession data) {

        data.setFeedbackCompleted();

        if (data.getTestCasesParsed() == data.getTestCasesTotal()) {
            sendFeedback(data);
        }
    }

    public static void feedbackHandleRegister(DataSession data, String id, JSONObject feedback) {

        if (!data.isFeedbackEnabled()) {
            return;
        }

        data.incTestCasesParsed();

        data.addTestCase(id, feedback);

        if (data.getTestCasesParsed() == data.getTestCasesTotal() && data.isFeedbackCompleted()) {
            sendFeedback(data);
        }
    }

    public static Optional<TestHandle> feedbackHandleCreate(DataSession data, String handle) {

        if (!data.isFeedbackEnabled()) {
            return Optional.empty();
        }

        var result = Optional.of(TestHandle.create(data, handle, "0:" + data.getTestCasesTotal()));

        data.incTestCasesTotal();

        return result;
    }

    private static void sendFeedback(DataSession data) {

        if (!data.isFeedbackEnabled()) {
            return;
        }

        HelperConnection.sendFeedbackRequest(data);
    }

    public static void activateStructure(DataSession data, String signature) {

        data.getInitializer().activate(signature);
    }

    public static Object[] getTestCase(DataSession data, Queue<String> arguments) {

        return data.getInitializer().getTestCase(data.getMethodNameSignature(), arguments);
    }
}
