package com.ecfeed;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class ChunkParserStream implements ChunkParser<Optional<Object[]>> {
    private static final String keyInfo = "info";
    private static final String keyInfoMethod = "method";
    private static final String keyInfoSession = "id";
    private static final String keyTestCase = "testCase";
    private static final String keyTestCaseValue = "value";

    private String[] argumentTypes = null;
    private String[] argumentNames = null;

    private Feedback feedback;

    public ChunkParserStream() {

    }
    public ChunkParserStream(Feedback feedback) {

        this.feedback = feedback;
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

        if (chunk.equals("")) {
            return Optional.empty();
        }

        JSONObject json = new JSONObject(chunk);

        return argumentTypes == null ? parseInfo(json) : parseTestCase(json);
    }

    private Optional<Object[]> parseInfo(JSONObject json) {

        if (json.keySet().contains(keyInfo)) {
            JSONObject value = new JSONObject(json.getString(keyInfo));

            if (value.has(keyInfoMethod)) {
                feedback.setMethodInfo(value.getString(keyInfoMethod));
                parseInfoArgumentTypes(value.getString(keyInfoMethod));
            }

            if (value.has(keyInfoSession)) {
                feedback.setTestSessionId(value.getString(keyInfoSession));
            }
        }

        return Optional.empty();
    }

    private void parseInfoArgumentTypes(String method) {
        String parsedMethod = method.split("[()]")[1];
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
        Object[] response = null;

        if (json.keySet().contains(keyTestCase)) {
            JSONArray value = json.getJSONArray(keyTestCase);
            response = new Object[argumentTypes.length + 1];

            for (int i = 0 ; i < value.length() ; i++) {
                response[i] = parseType(i, value.getJSONObject(i).getString(keyTestCaseValue));
            }

            FeedbackItem feedbackItem = new FeedbackItem(value.toString());

            response[response.length - 1] = feedbackItem;

            feedback.addResult(feedbackItem);
        }

        return Optional.ofNullable(response);
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
