package com.ecfeed;

import com.ecfeed.chunk.ChunkParser;
import com.ecfeed.chunk.ChunkParserExport;
import com.ecfeed.chunk.ChunkParserStream;
import com.ecfeed.connection.ConnectionHandler;
import com.ecfeed.connection.ConnectionHandlerDefault;
import com.ecfeed.session.dto.DataSession;
import com.ecfeed.session.DataSessionFacade;
import com.ecfeed.session.DataSessionFacadeDefault;
import com.ecfeed.queue.IterableTestQueue;
import com.ecfeed.queue.IterableTestQueueExport;
import com.ecfeed.queue.IterableTestQueueStream;
import com.ecfeed.session.dto.DataSessionConnection;
import com.ecfeed.system.SystemHandler;
import com.ecfeed.system.SystemHandlerDefault;
import com.ecfeed.type.TypeGenerator;

import java.nio.file.Path;

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

    public static SystemHandler getSystemHandler() {

        return SystemHandlerDefault.create();
    }

    public static DataSession getDataSession(DataSessionConnection connection, String model, String method, TypeGenerator generatorType) {

        return DataSession.create(connection, model, method, generatorType);
    }

    public static DataSessionConnection getDataSessionConnection(String httpAddress, Path keyStorePath, String keyStorePassword) {

        return DataSessionConnection.create(httpAddress, keyStorePath, keyStorePassword);
    }
}
