package com.ecfeed.parser;

import com.ecfeed.Config;
import com.ecfeed.data.FeedbackHandle;
import com.ecfeed.data.SessionData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class ChunkParserStream implements ChunkParser<Optional<Object[]>> {
    private SessionData sessionData;

    private String[] argumentTypes;
    private String[] argumentNames;

    private ChunkParserStream(SessionData sessionData) {

        this.sessionData = sessionData;
    }

    public static ChunkParserStream create(SessionData sessionData) {

        return new ChunkParserStream(sessionData);
    }

    @Override
    public String[] getMethodTypes() {

        return argumentTypes;
    }

    @Override
    public String[] getMethodNames() {

        return argumentNames;
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
            throw new RuntimeException("The data received from the generator is erroneous");
        }

        if (json.keySet().contains(Config.Key.reqTestInfoCase)) {
            return parseTestCase(json);
        }

        if (json.keySet().contains(Config.Key.reqTestStatus)) {
            return parseStatus(json);
        }

        if (json.keySet().contains(Config.Key.reqTestInfo)) {
            return parseInfo(json);
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseStatus(JSONObject json) {
        String value = json.getString(Config.Key.reqTestStatus);

        if (value.contains(Config.Key.reqTestStatusEnd)) {
            sessionData.feedbackSetComplete();
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseInfo(JSONObject json) {
        String value = json.getString(Config.Key.reqTestInfo);

        if (value.contains(Config.Key.reqTestInfoMethod)) {
            parseInfoArgumentTypes(value);
        }

        if (value.contains(Config.Key.reqTestInfoTimestamp)) {
            sessionData.setTimestamp(new JSONObject(value).getInt(Config.Key.reqTestInfoTimestamp));
        }

        if (value.contains(Config.Key.reqTestInfoSessionId)) {
            sessionData.setTestSessionId(new JSONObject(value).getString(Config.Key.reqTestInfoSessionId));
        }

        return Optional.empty();
    }

    private void parseInfoArgumentTypes(String method) {
        String parsedMethod;

        parsedMethod = new JSONObject(method).getString(Config.Key.reqTestInfoMethod);

        sessionData.setMethodNameQualified(parsedMethod);

        parsedMethod = parsedMethod.split("[()]")[1];
        String[] argument = parsedMethod.split(", ");

        argumentTypes = new String[argument.length];
        argumentNames = new String[argument.length];

        for (int i = 0 ; i < argument.length ; i++) {
            String[] parsedArgument = argument[i].split(" ");
            argumentTypes[i] = parsedArgument[0];
            argumentNames[i] = parsedArgument[1];
        }
    }

    private Optional<Object[]> parseTestCase(JSONObject json) {

        if (json.keySet().contains(Config.Key.reqTestInfoCase)) {
            Optional<FeedbackHandle> feedbackHandle = sessionData.feedbackHandleCreate(json.toString());
            JSONArray arguments = json.getJSONArray(Config.Key.reqTestInfoCase);

            if (feedbackHandle.isPresent()) {
                return parseTestCaseFeedback(arguments, feedbackHandle.get());
            }

            return parseTestCaseDefault(arguments);
        }

        return Optional.empty();
    }

    private Optional<Object[]> parseTestCaseDefault(JSONArray json) {
        Object[] response = new Object[argumentTypes.length];

        for (int i = 0 ; i < json.length() ; i++) {
            response[i] = parseType(i, json.getJSONObject(i).getString(Config.Key.reqTestInfoCaseValue));
        }

        return Optional.of(response);
    }

    private Optional<Object[]> parseTestCaseFeedback(JSONArray json, FeedbackHandle feedbackHandle) {
        Object[] response = new Object[argumentTypes.length + 1];

        for (int i = 0 ; i < json.length() ; i++) {
            response[i] = parseType(i, json.getJSONObject(i).getString(Config.Key.reqTestInfoCaseValue));
        }

        response[response.length - 1] = feedbackHandle;

        return Optional.of(response);
    }

    private Object parseType(int position, String value) {

        switch (argumentTypes[position]) {
            case "byte": return Byte.parseByte(value);
            case "short": return Short.parseShort(value);
            case "int": return Integer.parseInt(value);
            case "long": return Long.parseLong(value);
            case "float": return Float.parseFloat(value);
            case "double": return Double.parseDouble(value);
            case "boolean": return Boolean.parseBoolean(value);
            default: return value;
        }
    }
}
