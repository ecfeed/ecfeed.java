package com.ecfeed.chunk;

import java.util.Optional;

public interface ChunkParser<T> {

    Optional<T> parse(String chunk);
}
