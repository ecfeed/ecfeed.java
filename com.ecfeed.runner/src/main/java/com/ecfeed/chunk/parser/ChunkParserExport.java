package com.ecfeed.chunk.parser;

import java.util.Optional;

public class ChunkParserExport implements ChunkParser<Optional<String>> {

    private ChunkParserExport() { }

    public static ChunkParserExport create() {

        return new ChunkParserExport();
    }

    @Override
    public Optional<String> parse(String chunk) {

        return Optional.of(chunk);
    }
}
