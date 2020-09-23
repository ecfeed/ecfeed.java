package com.ecfeed.implementation;

import com.ecfeed.design.IterableTestStream;
import com.ecfeed.design.ChunkParser;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultIterableTestStream<T> implements IterableTestStream<T> {

    private final BlockingQueue<Optional<T>> parsedTestBuffer;
    private final ChunkParser<Optional<T>> chunkParser;
    private Optional<T> parsedTest;
    private boolean readyToSend;

    public DefaultIterableTestStream(ChunkParser<Optional<T>> chunkParser) {

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
            response = parsedTest.orElseThrow(() -> new IllegalArgumentException("The chunk could not be retrieved"));
        } else {
            hasNext();
            response = next();
        }

        readyToSend = false;
        return response;
    }

    @Override
    public void append(String chunk) {

        chunkParser.parse(chunk).ifPresent(this::appendParsedTest);
    }

    private void appendParsedTest(T parsedTest) {

        try {
            parsedTestBuffer.put(Optional.of(parsedTest));
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("The test could not be added", e);
        }
    }

    @Override
    public void terminate() {

        try {
            parsedTestBuffer.put(Optional.empty());
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("The test buffer could not be closed", e);
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