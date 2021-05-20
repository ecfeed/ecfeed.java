package com.ecfeed.chunk.parser;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.TestHandle;
import com.ecfeed.data.DataSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            throw new RuntimeException("The data received from the generator is erroneous");
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
            dataSession.feedbackSetComplete();
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
            argumentTypes[i] = parsedArgument[0];
            argumentNames[i] = parsedArgument[1];
        }

        dataSession.setArgumentTypes(argumentTypes);
        dataSession.setArgumentNames(argumentNames);
    }

    private Optional<Object[]> parseTestCase(JSONObject json) {

        if (json.keySet().contains(ConfigDefault.Key.reqTestInfoCase)) {
            Optional<TestHandle> feedbackHandle = dataSession.feedbackHandleCreate(json.toString());
            JSONArray arguments = json.getJSONArray(ConfigDefault.Key.reqTestInfoCase);

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
            response[i] = parseType(i, json.getJSONObject(i).getString(ConfigDefault.Key.reqTestInfoCaseValue));
        }

        return Optional.of(response);
    }

    private Optional<Object[]> parseTestCaseFeedback(JSONArray json, TestHandle testHandle) {
        Object[] response = new Object[argumentTypes.length + 1];

        for (int i = 0 ; i < json.length() ; i++) {
            response[i] = parseType(i, json.getJSONObject(i).getString(ConfigDefault.Key.reqTestInfoCaseValue));
        }

        response[response.length - 1] = testHandle;

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
