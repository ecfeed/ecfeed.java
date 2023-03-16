package com.ecfeed.chunk;

import com.ecfeed.Factory;
import com.ecfeed.TestHandle;
import com.ecfeed.config.ConfigDefault;
import com.ecfeed.data.DataSession;
import com.ecfeed.data.DataSessionFacade;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Optional;

public class ChunkParserStream implements ChunkParser<Object[]> {
    private final DataSessionFacade dataSessionFacade;

    private ChunkParserStream(DataSession dataSession) {

        dataSessionFacade = Factory.getDataSessionFacade(dataSession);
    }

    public static ChunkParserStream create(DataSession data) {

        return new ChunkParserStream(data);
    }

    @Override
    public Optional<Object[]> parse(String chunk) {

        if (chunk == null || chunk.equals("")) {
            return Optional.empty();
        }

        JSONObject json;

        try {
            json = new JSONObject(chunk);
        } catch (JSONException e) {
            throw new RuntimeException("The data received from the generator is erroneous!");
        }

        if (json.keySet().contains(ConfigDefault.Key.reqTestInfoCase)) {
            return parseTestCase(json);
        }

        if (json.keySet().contains(ConfigDefault.Key.reqTestStatus)) {
            return parseStatus(json);
        }

        if (json.keySet().contains(ConfigDefault.Key.reqTestInfo)) {
            return parseInfo(json);
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseStatus(JSONObject json) {
        String value = json.getString(ConfigDefault.Key.reqTestStatus);

        if (value.contains(ConfigDefault.Key.reqTestStatusEnd)) {
            dataSessionFacade.feedbackSetComplete();
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseInfo(JSONObject json) {
        String value = json.getString(ConfigDefault.Key.reqTestInfo);

        if (value.contains(ConfigDefault.Key.reqTestInfoMethod)) {
            parseInfoArgumentTypes(value);
            parseInfoMethodSignature();
        }

        if (value.contains(ConfigDefault.Key.reqTestInfoTimestamp)) {
            dataSessionFacade.getDataSession().setTimestamp(new JSONObject(value).getInt(ConfigDefault.Key.reqTestInfoTimestamp));
        }

        if (value.contains(ConfigDefault.Key.reqTestInfoSessionId)) {
            dataSessionFacade.getDataSession().setTestSessionId(new JSONObject(value).getString(ConfigDefault.Key.reqTestInfoSessionId));
        }

        if (value.contains(ConfigDefault.Key.reqTestInfoSignature)) {
            dataSessionFacade.activateStructure(new JSONObject(value).getString(ConfigDefault.Key.reqTestInfoSignature));
        }

        return Optional.empty();
    }

    private void parseInfoArgumentTypes(String method) {
        String parsedMethod;

        parsedMethod = new JSONObject(method).getString(ConfigDefault.Key.reqTestInfoMethod);

        dataSessionFacade.getDataSession().setMethodNameQualified(parsedMethod);

        parsedMethod = parsedMethod.split("[()]")[1];
        String[] argument = parsedMethod.split(",");

        for (int i = 0 ; i < argument.length ; i++) {
            String[] parsedArgument = argument[i].trim().split(" ");

            if (parsedArgument.length == 1) {
                parseInfoArgumentTypesDefault(parsedArgument[0]);
            } else {
                parseInfoArgumentTypesLegacy(parsedArgument[0], parsedArgument[1]);
            }
        }
    }

    private void parseInfoArgumentTypesLegacy(String type, String name) {

        if (type.equalsIgnoreCase("Structure")) {
            dataSessionFacade.getDataSession().addArgumentType(name);
        } else {
            dataSessionFacade.getDataSession().addArgumentType(type);
        }

        dataSessionFacade.getDataSession().addArgumentName(name);
    }

    private void parseInfoArgumentTypesDefault(String type) {

        dataSessionFacade.getDataSession().addArgumentType(type);
        dataSessionFacade.getDataSession().addArgumentName("arg" + dataSessionFacade.getDataSession().getArgumentNames().size());
    }

    private void parseInfoMethodSignature() {

        var methodName = dataSessionFacade.getDataSession().getMethodName().substring(dataSessionFacade.getDataSession().getMethodName().lastIndexOf("."));
        var methodTypes = dataSessionFacade.getDataSession().getArgumentTypes();
        var methodSignature = methodName + "(" + String.join(",", methodTypes) + ")";

        dataSessionFacade.getDataSession().setMethodNameSignature(methodSignature);
    }

    private Optional<Object[]> parseTestCase(JSONObject json) {

        if (json.keySet().contains(ConfigDefault.Key.reqTestInfoCase)) {
            Optional<TestHandle> feedbackHandle = dataSessionFacade.feedbackHandleCreate(json.toString());
            JSONArray arguments = json.getJSONArray(ConfigDefault.Key.reqTestInfoCase);

            return feedbackHandle.map(testHandle -> parseTestCaseWithFeedback(arguments, testHandle)).orElseGet(() -> parseTestCase(arguments));
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseTestCase(JSONArray json) {
        var response = new LinkedList<String>();

        for (int i = 0 ; i < json.length() ; i++) {
            response.add(json.getJSONObject(i).getString(ConfigDefault.Key.reqTestInfoCaseValue));
        }

        var test = dataSessionFacade.getTestCase(response);

        return Optional.of(test);
    }

    private Optional<Object[]> parseTestCaseWithFeedback(JSONArray json, TestHandle testHandle) {
        var response = new LinkedList<String>();

        for (int i = 0 ; i < json.length() ; i++) {
            response.add(json.getJSONObject(i).getString(ConfigDefault.Key.reqTestInfoCaseValue));
        }

        var test = dataSessionFacade.getTestCase(response);

        return Optional.of(parseTestCaseWithFeedbackExtendArray(test, testHandle));
    }

    private Object[] parseTestCaseWithFeedbackExtendArray(Object[] test, TestHandle testHandle) {

        var destArray = new Object[test.length + 1];
        System.arraycopy(test, 0, destArray, 0, test.length);
        destArray[destArray.length - 1] = testHandle;

        return destArray;
    }
}
