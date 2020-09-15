package com.ecfeed.runner.design;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface TestProvider {

    void validateConnection();

    List<String> getMethodNames(String methodName);
    List<String> getMethodTypes(String methodName);

    String getModel();
    String getGeneratorAddress();
    String getKeyStorePassword();
    Path getKeyStorePath();

    IteratorTestStream<String> export(String method, String generator, Map<String, Object> properties);
    IteratorTestStream<String> exportNWise(String method, Map<String, Object> properties);

    IteratorTestStream<Object[]> stream(String method, String generator, Map<String, Object> properties);
    IteratorTestStream<Object[]> streamNWise(String method, Map<String, Object> properties);

    void sendRequest(IteratorTestStream iterator, String request);
}
