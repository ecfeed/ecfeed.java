package com.ecfeed.session;

import com.ecfeed.Factory;
import com.ecfeed.TestHandle;
import com.ecfeed.config.ConfigDefault;
import com.ecfeed.connection.ConnectionHandler;
import com.ecfeed.session.dto.DataSession;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class DataSessionFacadeDefault implements DataSessionFacade {

    private final DataSession data;
    private final ConnectionHandler connectionHandler;

    private DataSessionFacadeDefault(DataSession data) {

        this.data = data;
        this.connectionHandler = Factory.getConnectionHandler();
    }

    public static DataSessionFacade create(DataSession data) {
        return new DataSessionFacadeDefault(data);
    }

    @Override
    public DataSession getDataSession() {

        return this.data;
    }

    @Override
    public String generateURLForTestData() {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForTestDataCore(requestBuilder);
        generateURLForTestDataParameters(requestBuilder);

        return requestBuilder.toString();
    }

    @Override
    public String generateURLForFeedback() {
        StringBuilder requestBuilder = new StringBuilder();

        generateURLForFeedbackCore(requestBuilder);
        generateURLForFeedbackParameters(requestBuilder);

        return requestBuilder.toString();
    }

    @Override
    public String generateURLForTestDataRequest() {
        JSONObject request = new JSONObject();

        request.put(ConfigDefault.Key.reqDataMode, data.getModel());
        request.put(ConfigDefault.Key.reqDataMethod, data.getMethodName());
        request.put(ConfigDefault.Key.reqDataUserData, generateURLForTestDataRequestUserData());

        data.getExportTemplate().ifPresent(e -> request.put(ConfigDefault.Key.parDataTemplate, e));

        try {
            return URLEncoder.encode(request.toString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The request could not be generated.");
        }
    }

    @Override
    public String generateBodyForFeedback() {
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

    @Override
    public void feedbackSetComplete() {

        data.setFeedbackCompleted();

        if (data.getTestCasesParsed() == data.getTestCasesTotal()) {
            sendFeedback();
        }
    }

    @Override
    public void feedbackHandleRegister(String id, JSONObject feedback) {

        if (!data.isFeedbackEnabled()) {
            return;
        }

        data.incTestCasesParsed();

        data.addTestCase(id, feedback);

        if (data.getTestCasesParsed() == data.getTestCasesTotal() && data.isFeedbackCompleted()) {
            sendFeedback();
        }
    }

    @Override
    public Optional<TestHandle> feedbackHandleCreate(String handle) {

        if (!data.isFeedbackEnabled()) {
            return Optional.empty();
        }

        var result = Optional.of(TestHandle.create(data, handle, "0:" + data.getTestCasesTotal()));

        data.incTestCasesTotal();

        return result;
    }

    @Override
    public void activateStructure(String signature) {

        data.getInitializer().activate(signature);
    }

    @Override
    public Object[] getTestCase(Queue<String> arguments) {

        return data.getInitializer().getTestCase(data.getMethodNameSignature(), arguments);
    }

    private void generateURLForTestDataCore(StringBuilder builder) {

        builder.append(getHttpAddress()).append("/").append(ConfigDefault.Key.urlService);
    }

    private void generateURLForTestDataParameters(StringBuilder builder) {
        String type = data.getExportTemplate().isPresent() ? ConfigDefault.Value.parRequestTypeExport : ConfigDefault.Value.parRequestTypeStream;

        builder.append("?");
        builder.append(ConfigDefault.Key.reqDataRequestType).append("=").append(type);
        builder.append("&");
        builder.append(ConfigDefault.Key.reqDataRequest).append("=").append(generateURLForTestDataRequest());
        builder.append("&");
        builder.append(ConfigDefault.Key.reqDataClient).append("=").append(ConfigDefault.Value.parClient);

        if (!data.getRunner().isBlank()) {
            if (data.getRunner().equalsIgnoreCase("basic")) {
                builder.append("&clientType=localTestRunner");
            } else if (data.getRunner().equalsIgnoreCase("team")) {
                builder.append("&clientType=localTestRunnerTeam");
            }
        }
    }

    private void generateURLForFeedbackCore(StringBuilder builder) {

        builder.append(getHttpAddress()).append("/").append(ConfigDefault.Key.urlFeedback);

        if (!data.getRunner().isBlank()) {
            if (data.getRunner().equalsIgnoreCase("basic")) {
                builder.append("?clientType=localTestRunner");
            } else if (data.getRunner().equalsIgnoreCase("team")) {
                builder.append("?clientType=localTestRunnerTeam");
            }
        }
    }

    private void generateURLForFeedbackParameters(StringBuilder builder) { }

    private String generateURLForTestDataRequestUserData() {
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

    private String getHttpAddress() {
        String httpAddress = data.getHttpAddress();

        if (httpAddress == null) {
            throw new IllegalArgumentException("The generator address is not defined.");
        }

        if (!httpAddress.startsWith("https://")) {
            throw new IllegalArgumentException("The generator address should start with https://.");
        }

        return httpAddress;
    }

    private void parseFeedbackElement(JSONObject json, String key, Object value) {

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

    private void sendFeedback() {

        if (!data.isFeedbackEnabled()) {
            return;
        }

        connectionHandler.getFeedbackRequest(data);
    }
}
