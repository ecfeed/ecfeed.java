package com.ecfeed.implementation;

import com.ecfeed.design.ChunkParser;

import java.util.Optional;

public class ExportChunkParser implements ChunkParser<Optional<String>> {

    @Override
    public Optional<String> parse(String chunk) {

        return Optional.of(chunk);
    }
}
