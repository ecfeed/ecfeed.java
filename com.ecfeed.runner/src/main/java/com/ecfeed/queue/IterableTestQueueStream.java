package com.ecfeed.queue;

import com.ecfeed.Factory;
import com.ecfeed.chunk.ChunkParser;
import com.ecfeed.data.DataSession;

public class IterableTestQueueStream<T> extends IterableTestQueueAbstract<T> {

    private IterableTestQueueStream(ChunkParser<T> chunkParser) {

        super(chunkParser);
    }

    public static IterableTestQueue<Object[]> create(DataSession data) {

        return new IterableTestQueueStream<>(Factory.getChunkParserStream(data));
    }
}
