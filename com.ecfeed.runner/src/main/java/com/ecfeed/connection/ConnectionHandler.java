package com.ecfeed.connection;

import com.ecfeed.session.dto.DataSession;
import com.ecfeed.session.dto.DataSessionConnection;
import com.ecfeed.queue.IterableTestQueue;

import java.io.InputStream;

public interface ConnectionHandler {

    InputStream getChunkStreamForTestData(DataSession dataSession);

    InputStream getFeedbackRequest(DataSession dataSession);

    DataSession sendMockRequest(DataSessionConnection connection, String model, String method);

    void processChunkStream(IterableTestQueue<?> iterator, InputStream chunkInputStream);

    void validateConnection(DataSessionConnection connection);
}
