package com.ecfeed.structure;

import com.ecfeed.structure.dto.Structure;

import java.util.Queue;
import java.util.Set;

public interface StructureInitializer {

    void source(Class... sourceClass);

    void source(String... sourcePackage);

    void activate(String... signatureStructure);

    <T> T instantiate(Class<T> type, Queue<String> arguments);

    Object instantiate(String signatureStructure, Queue<String> arguments);

    Object[] getTestCase(String signatureMethod, Queue<String> arguments);

    Set<Structure> getStructuresRaw();

    Set<Structure> getStructuresActive();

    Set<String> getNamesSimpleRaw();

    Set<String> getNamesSimpleActive();

    Set<String> getNamesQualifiedRaw();

    Set<String> getNamesQualifiedActive();
}
