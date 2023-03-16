package com.ecfeed.queue;

import java.util.Iterator;

public interface IterableTestQueue<T> extends Iterator<T>, Iterable<T>  {

    Iterator<T> iterator();

    boolean hasNext();

    T next();

    void append(String chunk);

    void terminate();
}
