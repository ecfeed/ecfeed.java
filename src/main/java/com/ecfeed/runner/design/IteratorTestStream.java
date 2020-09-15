package com.ecfeed.runner.design;

import java.util.Iterator;

public interface IteratorTestStream<T> extends Iterator<T>, Iterable<T> {

    void append(String chunk);
    void terminate();
}
