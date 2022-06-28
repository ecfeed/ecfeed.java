package com.ecfeed.chunk.parser;

public interface ChunkParser<T> {

    T parse(String chunk);
}
