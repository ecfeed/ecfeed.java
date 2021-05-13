package com.ecfeed.helper;

import java.util.Map;

public final class CollectionHelper {

    private CollectionHelper() {
        throw new RuntimeException("The helper class cannot be instantiated");
    }

    public static void addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }
}
