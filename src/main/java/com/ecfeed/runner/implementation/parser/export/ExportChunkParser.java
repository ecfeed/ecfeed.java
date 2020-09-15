package com.ecfeed.runner.implementation.parser.export;

import com.ecfeed.runner.design.parser.ChunkParser;

import java.util.Optional;

public class ExportChunkParser implements ChunkParser<Optional<String>> {

    @Override
    public Optional<String> parse(String chunk) {

        return Optional.of(chunk);
    }
}
