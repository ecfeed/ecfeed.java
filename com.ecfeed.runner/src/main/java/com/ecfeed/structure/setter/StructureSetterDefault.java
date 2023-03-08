package com.ecfeed.structure.setter;

import com.ecfeed.structure.dto.Structure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StructureSetterDefault implements StructureSetter {

    private StructureSetterDefault() {
    }

    public static StructureSetter get() {

        return new StructureSetterDefault();
    }

    @Override
    public Structure parse(Class source) {
        var structure = new Structure();

        structure.setSource(source);
        structure.setNameQualified(getNameQualified(source));
        structure.setNameSimple(getNameSimple(source));
        structure.setConstructors(getConstructors(source));

        return structure;
    }

    private String getNameQualified(Class source) {

        return source.getCanonicalName();
    }

    private String getNameSimple(Class source) {
        var nameCanonical = source.getCanonicalName().split("\\.");

        return nameCanonical[nameCanonical.length - 1];
    }

    private Map<String, Constructor> getConstructors(Class source) {
        Map<String, Constructor> constructors = new HashMap<>();

       for (Constructor constructor : source.getConstructors()) {
           constructors.put(getNameSimple(source) + parseConstructor(constructor), constructor);
       }

       return constructors;
    }

    private String parseConstructor(Constructor constructor) {
        var parameters = constructor.getParameters();

        var parametersParsed = Arrays.stream(parameters).map(this::parseConstructorParameter).collect(Collectors.toList());

        return "(" + String.join(",", parametersParsed) + ")";
    }

    private String parseConstructorParameter(Parameter parameter) {

        return getNameSimple(parameter.getType());
    }
}
