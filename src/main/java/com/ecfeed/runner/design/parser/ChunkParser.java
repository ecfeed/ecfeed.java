package com.ecfeed.runner.design.parser;

public interface ChunkParser<T> {

    T parse(String chunk);

    default String[] getMethodNames() {

        return new String[0];
    }

    default String[] getMethodTypes() {

        return new String[0];
    }
}
