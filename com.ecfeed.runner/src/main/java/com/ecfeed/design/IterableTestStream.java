package com.ecfeed.design;

import java.util.Iterator;

public interface IterableTestStream<T> extends Iterator<T>, Iterable<T> {

    void append(String chunk);
    void terminate();
}
