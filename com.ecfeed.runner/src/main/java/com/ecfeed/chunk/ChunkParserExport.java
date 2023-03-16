package com.ecfeed.chunk;

import java.util.Optional;

public class ChunkParserExport implements ChunkParser<String> {

    private ChunkParserExport() { }

    public static ChunkParserExport create() {

        return new ChunkParserExport();
    }

    @Override
    public Optional<String> parse(String chunk) {

        return Optional.of(chunk);
    }
}
