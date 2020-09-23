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

    Iterable<String> export(String method, String generator, ExportTemplate exportTemplate, Map<String, Object> properties);
    Iterable<String> exportNWise(String method, ExportTemplate exportTemplate, Map<String, Object> properties);
    Iterable<String> exportCartesian(String method, ExportTemplate exportTemplate, Map<String, Object> properties);
    Iterable<String> exportRandom(String method, ExportTemplate exportTemplate, Map<String, Object> properties);
    Iterable<String> exportStatic(String method, ExportTemplate exportTemplate, Map<String, Object> properties);

    Iterable<Object[]> generate(String method, String generator, Map<String, Object> properties);
    Iterable<Object[]> generateNWise(String method, Map<String, Object> properties);
    Iterable<Object[]> generateCartesian(String method, Map<String, Object> properties);
    Iterable<Object[]> generateRandom(String method, Map<String, Object> properties);
    Iterable<Object[]> generateStatic(String method, Map<String, Object> properties);
}
