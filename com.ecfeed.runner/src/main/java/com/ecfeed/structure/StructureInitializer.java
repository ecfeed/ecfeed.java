package com.ecfeed.structure;

import com.ecfeed.structure.dto.Structure;

import java.util.Set;

public interface StructureInitializer {

    void activate(String... signatureStructure);

    Set<Structure> getStructuresRaw();

    Set<Structure> getStructuresActive();

    void addSource(Class... sourceClass);

    void addSource(String... sourcePackage);

    Set<String> getNamesSimpleRaw();

    Set<String> getNamesSimpleActive();

    Set<String> getNamesQualifiedRaw();

    Set<String> getNamesQualifiedActive();

    void validateIntegrityStructure(String signatureStructure);

    void validateIntegrityMethod(String signatureMethod);

    int getNumberOfParameters(String signatureStructure);

//    Object instantiateStructure(String signatureStructure, String[] parameters, int index);

//    Object[] instantiateMethod(String signatureMethod, String[] parameters, int index);
}
