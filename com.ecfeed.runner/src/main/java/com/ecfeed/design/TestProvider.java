package com.ecfeed.design;

import com.ecfeed.constant.ExportTemplate;

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

    IteratorTestStream<Object[]> generate(String method, String generator, Map<String, Object> properties);
    IteratorTestStream<Object[]> generateNWise(String method, Map<String, Object> properties);
    IteratorTestStream<Object[]> generateCartesian(String method, Map<String, Object> properties);
    IteratorTestStream<Object[]> generateRandom(String method, Map<String, Object> properties);
    IteratorTestStream<Object[]> generateStatic(String method, Map<String, Object> properties);
}
