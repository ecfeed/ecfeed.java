package com.ecfeed.structure.setter;

import com.ecfeed.structure.dto.Structure;

import java.io.*;
import java.util.*;

public class StructuresSetterDefault implements StructuresSetter {

    private StructureSetter structureSetter;
    private ClassLoader classLoader;

    {
        classLoader = ClassLoader.getSystemClassLoader();
    }

    private StructuresSetterDefault(StructureSetter structureSetter) {

        this.structureSetter = structureSetter;
    }

    public static StructuresSetter get(StructureSetter structureSetter) {

        return new StructuresSetterDefault(structureSetter);
    }

    @Override
    public List<Structure> parse(Class... sourceClass) {
        List<Structure> structures = new ArrayList<>();

        Arrays.stream(sourceClass).forEach(e -> addStructures(e, structures));

        return structures;
    }

    private void addStructures(Class sourceClass, List<Structure> structures) {

        structures.add(structureSetter.parse(sourceClass));

        Arrays.stream(sourceClass.getDeclaredClasses()).forEach(e -> addStructures(e, structures));
    }

    @Override
    public List<Structure> parse(String... sourcePackage) {
        List<Structure> structures = new ArrayList<>();

        Arrays.stream(sourcePackage).forEach(e -> addStructures(e, structures));

        return structures;
    }

    private void addStructures(String sourcePackage, List<Structure> structures) {

        try (var streamReader = getPackageReader(sourcePackage)) {
           processPackageElements(sourcePackage, streamReader, structures);
        } catch (IOException e) {
            throw new RuntimeException("The package '" + sourcePackage + "' could not be processed!");
        }
    }

    private BufferedReader getPackageReader(String sourcePackage) {
        var sourceParsed = sourcePackage.replaceAll("\\.", "/");

        var sourceStream = classLoader.getResourceAsStream(sourceParsed);

        if (sourceStream == null) {
            throw new RuntimeException("The package '" + sourcePackage + "' could not be found!");
        }

        return new BufferedReader(new InputStreamReader(sourceStream));
    }

    private void processPackageElements(String sourcePackage, BufferedReader streamReader, List<Structure> structures) {

        streamReader.lines().forEach(e -> {
            if (e.endsWith(".class")) {
                processPackageElementsClass(e, sourcePackage, structures);
            } else {
                processPackageElementsPackage(e, sourcePackage, structures);
            }
        });
    }

    private void processPackageElementsClass(String element, String sourcePackage, List<Structure> structures) {

        structures.add(structureSetter.parse(getClass(element, sourcePackage)));
    }

    private void processPackageElementsPackage(String element, String sourcePackage, List<Structure> structures) {

        addStructures(sourcePackage + "." + element, structures);
    }

    private Class getClass(String element, String sourcePackage) {
        var className = element.substring(0, element.lastIndexOf('.'));
        var classNameQualified = sourcePackage + "." + className;

        try {
            return Class.forName(classNameQualified);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("The class '" + classNameQualified + "' could not be loaded!");
        }
    }
}
