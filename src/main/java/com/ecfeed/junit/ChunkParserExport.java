package com.ecfeed.junit;

import java.util.Optional;

public class ChunkParserExport implements ChunkParser<Optional<String>> {

    @Override
    public Optional<String> parse(String chunk) {

        return Optional.of(chunk);
    }
}
