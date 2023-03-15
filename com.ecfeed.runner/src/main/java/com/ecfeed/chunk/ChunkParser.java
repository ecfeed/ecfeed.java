package com.ecfeed.chunk;

public interface ChunkParser<T> {

    T parse(String chunk);
}
