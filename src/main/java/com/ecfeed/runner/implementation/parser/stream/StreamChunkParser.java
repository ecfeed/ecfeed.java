package com.ecfeed.runner.implementation.parser.stream;

import com.ecfeed.runner.design.parser.ChunkParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class StreamChunkParser implements ChunkParser<Optional<Object[]>> {
    private static final String keyInfo = "info";
    private static final String keyInfoMethod = "method";
    private static final String keyTestCase = "testCase";
    private static final String keyTestCaseValue = "value";

    private String[] argumentTypes = null;

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
            String value = json.getString(keyInfo);

            if (value.contains(keyInfoMethod)) {
                parseInfoArgumentTypes(value);
            }
        }

        return Optional.empty();
    }

    private void parseInfoArgumentTypes(String method) {

        String parsedMethod;

        parsedMethod = new JSONObject(method).getString(keyInfoMethod);
        parsedMethod = parsedMethod.split("[\\(\\)]")[1];
        String[] argumentArray = parsedMethod.split(", ");

        for (int i = 0 ; i < argumentArray.length ; i++) {
            argumentArray[i] = argumentArray[i].split(" ")[0];
        }

        argumentTypes = argumentArray;
    }

    private Optional<Object[]> parseTestCase(JSONObject json) {
        Object[] response = null;

        if (json.keySet().contains(keyTestCase)) {
            JSONArray value = json.getJSONArray(keyTestCase);
            response = new Object[argumentTypes.length];

            for (int i = 0 ; i < value.length() ; i++) {
                response[i] = parseType(i, value.getJSONObject(i).getString(keyTestCaseValue));
            }
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
