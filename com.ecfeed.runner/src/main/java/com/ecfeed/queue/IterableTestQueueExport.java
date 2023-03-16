package com.ecfeed.queue;

import com.ecfeed.Factory;
import com.ecfeed.chunk.ChunkParser;

public class IterableTestQueueExport<T> extends IterableTestQueueAbstract<T> {

    private IterableTestQueueExport(ChunkParser<T> chunkParser) {

        super(chunkParser);
    }

    public static IterableTestQueueExport<java.lang.String> create() {

        return new IterableTestQueueExport<>(Factory.getChunkParserExport());
    }
}
