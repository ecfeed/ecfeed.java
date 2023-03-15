package com.ecfeed.queue;

import com.ecfeed.chunk.ChunkParser;
import com.ecfeed.chunk.ChunkParserExport;
import com.ecfeed.chunk.ChunkParserStream;
import com.ecfeed.data.DataSession;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IterableTestQueue<T> implements Iterator<T>, Iterable<T> {

    private final BlockingQueue<Optional<T>> parsedTestBuffer;
    private final ChunkParser<Optional<T>> chunkParser;
    private Optional<T> parsedTest;
    private boolean readyToSend;

    private IterableTestQueue(ChunkParser<Optional<T>> chunkParser) {

        this.parsedTestBuffer = new LinkedBlockingQueue<>();
        this.chunkParser = chunkParser;
    }

    public static IterableTestQueue<String> createForExport() {

        return new IterableTestQueue<>(ChunkParserExport.create());
    }

    public static IterableTestQueue<Object[]> createForStream(DataSession dataSession) {

        return new IterableTestQueue<>(ChunkParserStream.create(dataSession));
    }

    @Override
    public Iterator<T> iterator() {

        return this;
    }

    @Override
    public boolean hasNext() {
        boolean response;

        if (readyToSend) {
            response = parsedTest.isPresent();
        } else {
            response = (parsedTest = getNextTest()).isPresent();
        }

        readyToSend = true;
        return response;
    }

    @Override
    public T next() {
        T response;

        if (readyToSend) {
            response = parsedTest.orElseThrow(() -> new IllegalArgumentException("The chunk could not be retrieved"));
        } else {
            response = hasNext() ? next() : null;
        }

        readyToSend = false;
        return response;
    }

    public void append(String chunk) {

        chunkParser.parse(chunk).ifPresent(this::appendParsedTest);
    }

    public void terminate() {

        try {
            parsedTestBuffer.put(Optional.empty());
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("The test buffer could not be closed", e);
        }
    }

    private void appendParsedTest(T parsedTest) {

        try {
            parsedTestBuffer.put(Optional.of(parsedTest));
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("The test could not be added", e);
        }
    }

    private Optional<T> getNextTest() {

        try {
            return parsedTestBuffer.take();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("The chunk could not be parsed", e);
        }
    }
}
