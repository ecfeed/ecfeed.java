package com.ecfeed.structure;

import com.ecfeed.structure.dto.Structure;

import java.util.Set;

public interface StructureInitializer {

    void addSource(Class... sourceClass);

    void addSource(String... sourcePackage);

    Set<Structure> getStructures();

    Set<String> getNamesSimple();

    Set<String> getNamesQualified();

    Object initialize(String name, String constructor);
}
