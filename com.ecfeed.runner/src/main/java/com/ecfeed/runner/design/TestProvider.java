package com.ecfeed.runner.design;

import com.ecfeed.runner.constant.ExportTemplate;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface TestProvider {

    void validateConnection();

    List<String> getMethodNames(String methodName);
    List<String> getMethodTypes(String methodName);

    String getModel();
    String getGeneratorAddress();
    Path getKeyStorePath();

    IteratorTestStream<String> export(String method, String generator, ExportTemplate exportTemplate, Map<String, Object> properties);
    IteratorTestStream<String> exportNWise(String method, ExportTemplate exportTemplate, Map<String, Object> properties);
    IteratorTestStream<String> exportCartesian(String method, ExportTemplate exportTemplate, Map<String, Object> properties);
    IteratorTestStream<String> exportRandom(String method, ExportTemplate exportTemplate, Map<String, Object> properties);
    IteratorTestStream<String> exportStatic(String method, ExportTemplate exportTemplate, Map<String, Object> properties);

    IteratorTestStream<Object[]> stream(String method, String generator, Map<String, Object> properties);
    IteratorTestStream<Object[]> streamNWise(String method, Map<String, Object> properties);
    IteratorTestStream<Object[]> streamCartesian(String method, Map<String, Object> properties);
    IteratorTestStream<Object[]> streamRandom(String method, Map<String, Object> properties);
    IteratorTestStream<Object[]> streamStatic(String method, Map<String, Object> properties);
}
