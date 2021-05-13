package com.ecfeed.parser;

import com.ecfeed.data.SessionData;
import com.ecfeed.parser.ChunkParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class ChunkParserStream implements ChunkParser<Optional<Object[]>> {
    private static final String keyInfo = "info";
    private static final String keyInfoMethod = "method";
    private static final String keyInfoTestSessionId = "testSessionId";
    private static final String keyInfoTimestamp = "timestamp";
    private static final String keyTestCase = "testCase";
    private static final String keyTestCaseValue = "value";

    private SessionData sessionData;

    private ChunkParserStream(SessionData sessionData) {

        this.sessionData = sessionData;
    }

    public static ChunkParserStream create(SessionData sessionData) {

        return new ChunkParserStream(sessionData);
    }

    @Override
    public String[] getMethodTypes() {

        return sessionData.getArgumentTypes();
    }

    @Override
    public String[] getMethodNames() {

        return sessionData.getArgumentNames();
    }

    @Override
    public Optional<Object[]> parse(String chunk) {

        if (chunk.equals("")) {
            return Optional.empty();
        }

        JSONObject json = new JSONObject(chunk);

        return sessionData.getArgumentTypes() == null ? parseInfo(json) : parseTestCase(json);
    }

    private Optional<Object[]> parseInfo(JSONObject json) {

        if (json.keySet().contains(keyInfo)) {
            String value = json.getString(keyInfo);

            if (value.contains(keyInfoMethod)) {
                parseInfoArgumentTypes(value);
            }

            if (value.contains(keyInfoTestSessionId)) {
                sessionData.setTestSessionId(new JSONObject(value).getString(keyInfoTestSessionId));
            }

            if (value.contains(keyInfoTimestamp)) {
                sessionData.setTimestamp(new JSONObject(value).getInt(keyInfoTimestamp));
            }
        }

        return Optional.empty();
    }

    private void parseInfoArgumentTypes(String method) {
        String parsedMethod;

        parsedMethod = new JSONObject(method).getString(keyInfoMethod);

        sessionData.setMethodNameQualified(parsedMethod);

        parsedMethod = parsedMethod.split("[()]")[1];
        String[] argument = parsedMethod.split(", ");

        sessionData.setArgumentTypes(new String[argument.length]);
        sessionData.setArgumentNames(new String[argument.length]);

        for (int i = 0 ; i < argument.length ; i++) {
            String[] parsedArgument = argument[i].split(" ");
            sessionData.getArgumentTypes()[i] = parsedArgument[0];
            sessionData.getArgumentNames()[i] = parsedArgument[1];
        }
    }

    private Optional<Object[]> parseTestCase(JSONObject json) {
        Object[] response = null;

        if (json.keySet().contains(keyTestCase)) {
            JSONArray value = json.getJSONArray(keyTestCase);
            response = new Object[sessionData.getArgumentTypes().length];

            for (int i = 0 ; i < value.length() ; i++) {
                response[i] = parseType(i, value.getJSONObject(i).getString(keyTestCaseValue));
            }
        }

        return Optional.ofNullable(response);
    }



    private Object parseType(int position, String value) {

        switch (sessionData.getArgumentTypes()[position]) {
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
