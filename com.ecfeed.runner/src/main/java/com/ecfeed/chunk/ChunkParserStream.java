package com.ecfeed.chunk;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.TestHandle;
import com.ecfeed.data.DataHelper;
import com.ecfeed.data.DataSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

public class ChunkParserStream implements ChunkParser<Optional<Object[]>> {
    private DataSession dataSession;

    private String[] argumentTypes;
    private String[] argumentNames;

    private ChunkParserStream(DataSession dataSession) {

        this.dataSession = dataSession;
    }

    public static ChunkParserStream create(DataSession dataSession) {

        return new ChunkParserStream(dataSession);
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
            DataHelper.feedbackSetComplete(dataSession);
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseInfo(JSONObject json) {
        String value = json.getString(ConfigDefault.Key.reqTestInfo);

        if (value.contains(ConfigDefault.Key.reqTestInfoMethod)) {
            parseInfoArgumentTypes(value);
        }

        if (value.contains(ConfigDefault.Key.reqTestInfoTimestamp)) {
            dataSession.setTimestamp(new JSONObject(value).getInt(ConfigDefault.Key.reqTestInfoTimestamp));
        }

        if (value.contains(ConfigDefault.Key.reqTestInfoSessionId)) {
            dataSession.setTestSessionId(new JSONObject(value).getString(ConfigDefault.Key.reqTestInfoSessionId));
        }

        if (value.contains(ConfigDefault.Key.reqTestInfoSignature)) {
            DataHelper.activateStructure(dataSession, new JSONObject(value).getString(ConfigDefault.Key.reqTestInfoSignature));
        }

        return Optional.empty();
    }

    private void parseInfoArgumentTypes(String method) {
        String parsedMethod;

        parsedMethod = new JSONObject(method).getString(ConfigDefault.Key.reqTestInfoMethod);

        dataSession.setMethodNameQualified(parsedMethod);

        parsedMethod = parsedMethod.split("[()]")[1];
        String[] argument = parsedMethod.split(", ");

        argumentTypes = new String[argument.length];
        argumentNames = new String[argument.length];

        for (int i = 0 ; i < argument.length ; i++) {
            String[] parsedArgument = argument[i].split(" ");
            dataSession.addArgumentType(parsedArgument[0]);
            dataSession.addArgumentName(parsedArgument[1]);
        }

        dataSession.setMethodNameSignature(parseInfoMethodSignature());
    }

    private String parseInfoMethodSignature() {
        var parsedName = dataSession.getMethodName().substring(dataSession.getMethodName().lastIndexOf("."));
        var parsedTypes = Arrays.asList(argumentTypes);
        return parsedName + "(" + String.join(",", parsedTypes) + ")";
    }

    private Optional<Object[]> parseTestCase(JSONObject json) {

        if (json.keySet().contains(ConfigDefault.Key.reqTestInfoCase)) {
            Optional<TestHandle> feedbackHandle = DataHelper.feedbackHandleCreate(dataSession, json.toString());
            JSONArray arguments = json.getJSONArray(ConfigDefault.Key.reqTestInfoCase);

            if (feedbackHandle.isPresent()) {
                return parseTestCaseFeedback(arguments, feedbackHandle.get());
            }

            return parseTestCase(arguments);
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseTestCase(JSONArray json) {
        var response = new LinkedList<String>();

        for (int i = 0 ; i < json.length() ; i++) {
            response.add(json.getJSONObject(i).getString(ConfigDefault.Key.reqTestInfoCaseValue));
        }

        var test = DataHelper.getTestCase(dataSession, response);

        return Optional.of(test);
    }

    private Optional<Object[]> parseTestCaseFeedback(JSONArray json, TestHandle testHandle) {
        var response = new LinkedList<String>();

        for (int i = 0 ; i < json.length() ; i++) {
            response.add(json.getJSONObject(i).getString(ConfigDefault.Key.reqTestInfoCaseValue));
        }

        var test = DataHelper.getTestCase(dataSession, response);

        Object[] destArray = new Object[test.length + 1];
        System.arraycopy(test, 0, destArray, 0, test.length);
        destArray[destArray.length - 1] = testHandle;

        return Optional.of(destArray);
    }
}
