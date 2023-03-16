package com.ecfeed.queue;

import com.ecfeed.chunk.ChunkParser;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class IterableTestQueueAbstract<T> implements IterableTestQueue<T> {
    protected final BlockingQueue<Optional<T>> parsedTestBuffer;
    protected final ChunkParser<T> chunkParser;
    protected Optional<T> parsedTest;
    protected boolean readyToSend;

    protected IterableTestQueueAbstract(ChunkParser<T> chunkParser) {

        this.parsedTestBuffer = new LinkedBlockingQueue<>();
        this.chunkParser = chunkParser;
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
            response = parsedTest.orElseThrow(() -> new IllegalArgumentException("The chunk could not be retrieved!"));
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
