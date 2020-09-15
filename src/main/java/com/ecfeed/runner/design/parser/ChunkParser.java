package com.ecfeed.runner.design.parser;

public interface ChunkParser<T> {

    T parse(String chunk);
}
