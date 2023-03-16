package com.ecfeed;

import com.ecfeed.chunk.ChunkParser;
import com.ecfeed.chunk.ChunkParserExport;
import com.ecfeed.chunk.ChunkParserStream;
import com.ecfeed.connection.ConnectionHandler;
import com.ecfeed.connection.ConnectionHandlerDefault;
import com.ecfeed.data.DataSession;
import com.ecfeed.data.DataSessionFacade;
import com.ecfeed.data.DataSessionFacadeDefault;
import com.ecfeed.queue.IterableTestQueue;
import com.ecfeed.queue.IterableTestQueueExport;
import com.ecfeed.queue.IterableTestQueueStream;

public class Factory {

    private Factory() {
    }

    public static ChunkParser<Object[]> getChunkParserStream(DataSession data) {

        return ChunkParserStream.create(data);
    }

    public static ChunkParser<String> getChunkParserExport() {

        return ChunkParserExport.create();
    }

    public static ConnectionHandler getConnectionHandler() {

        return ConnectionHandlerDefault.create();
    }

    public static DataSessionFacade getDataSessionFacade(DataSession data) {

        return DataSessionFacadeDefault.create(data);
    }

    public static IterableTestQueue<Object[]> getIterableTestQueueStream(DataSession data) {

        return IterableTestQueueStream.create(data);
    }

    public static IterableTestQueue<String> getIterableTestQueueExport() {

        return IterableTestQueueExport.create();
    }
}
