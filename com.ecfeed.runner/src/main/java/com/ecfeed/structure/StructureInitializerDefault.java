package com.ecfeed.structure;

import com.ecfeed.structure.dto.Structure;
import com.ecfeed.structure.setter.StructureSetterDefault;
import com.ecfeed.structure.setter.StructuresSetter;
import com.ecfeed.structure.setter.StructuresSetterDefault;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StructureInitializerDefault implements StructureInitializer {

    private StructuresSetter structuresSetter;
    private Set<Structure> structures;

    {
        structuresSetter = StructuresSetterDefault.get(StructureSetterDefault.get());
        structures = new HashSet<>();
    }

    @Override
    public void source(Class... source) {
        var structures = structuresSetter.parse(source);

        for (var structure : structures) {
            addStructure(structure);
        }
    }

    @Override
    public void source(String... source) {
        var structures = structuresSetter.parse(source);

        for (var structure : structures) {
            addStructure(structure);
        }
    }

    @Override
    public void activate(String... signatureStructure) {

        structuresSetter.activate(structures, signatureStructure);
    }

    private void addStructure(Structure source) {
        var success = structures.add(source);

        if (!success) {

            for (Structure structure : structures) {
                if (structure.equals(source)) {
                    throw new RuntimeException("The structure '" + source.getNameQualified() + "' could not be added! " +
                            "There is at least one additional structure with the same name, i.e. '" + structure.getNameQualified() + "'.");
                }
            }

            throw new RuntimeException("The structure '" + source.getNameQualified() + "' could not be added!");
        }
    }

    @Override
    public Set<Structure> getStructuresRaw() {

        return structures;
    }

    @Override
    public Set<Structure> getStructuresActive() {

        return structures.stream().filter(Structure::isActive).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesSimpleRaw() {

        return structures.stream().map(Structure::getNameSimple).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesSimpleActive() {

        return structures.stream().filter(Structure::isActive).map(Structure::getNameSimple).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesQualifiedRaw() {

        return structures.stream().map(Structure::getNameQualified).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesQualifiedActive() {

        return structures.stream().filter(Structure::isActive).map(Structure::getNameQualified).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("\n");

        structures.forEach(e -> builder.append(e.toString() + "\n"));

        return builder.toString();
    }

    @Override
    public <T> T instantiate(Class<T> type, Queue<String> arguments) {

        return type.cast(instantiate(getTypeName(type), arguments));
    }

    @Override
    public Object instantiate(String signature, Queue<String> arguments) {
        Object element = instantiate(getStructure(signature), arguments);

        if (arguments.size() > 0) {
            throw new RuntimeException("The list of parameters is too long! Parameters remaining: '" + arguments.size() + "'.");
        }

        return element;
    }

    private Object instantiate(Structure structure, Queue<String> parameters) {

        if (!structure.isActive()) {
            throw new RuntimeException("The required structure '" + structure.getNameSimple() + "' has not been activated!'");
        }

        var constructorParameters = new LinkedList<>();

        for (var parameter : structure.getActiveConstructor().getParameters()) {
            constructorParameters.add(getValue(parameter.getType(), parameters));
        }

        try {
            return structure.getActiveConstructor().newInstance(constructorParameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException("The structure '" + structure.getNameSimple() + "' could not be instantiated!");
        }
    }

    private Structure getStructure(String signature) {

        for (var structure : structures) {
            if (structure.getNameSimple().equalsIgnoreCase(signature)) {
                return structure;
            }
        }

        throw new RuntimeException("The required structure '" + signature + "' could not be found in the source!");
    }

    private Object getValue(Class<?> type, Queue<String> parameters) {
        var typeName = getTypeName(type);

        switch (typeName) {
            case "byte":
                return parsePrimitive(parameters, typeName, Byte::parseByte);
            case "short":
                return parsePrimitive(parameters, typeName, Short::parseShort);
            case "int":
                return parsePrimitive(parameters, typeName, Integer::parseInt);
            case "long":
                return parsePrimitive(parameters, typeName, Long::parseLong);
            case "float":
                return parsePrimitive(parameters, typeName, Float::parseFloat);
            case "double":
                return parsePrimitive(parameters, typeName, Double::parseDouble);
            case "boolean":
                return parsePrimitive(parameters, typeName, Boolean::parseBoolean);
            case "char":
                return getPrimitive(parameters).charAt(0);
            case "String":
                return parameters.poll();
            default: {
                return instantiate(getStructure(typeName), parameters);
            }
        }
    }

    private String getTypeName(Class<?> type) {
        var nameCanonical = type.getCanonicalName();
        var nameSimple = nameCanonical.replaceAll(" ", "");

        if (nameSimple.contains(".")) {
            return nameSimple.substring(nameCanonical.lastIndexOf(".") + 1);
        }

        return nameSimple;
    }

    private String getPrimitive(Queue<String> parameters) {
        var value = parameters.poll();

        if (value == null) {
            throw new RuntimeException("Primitive types cannot accept null values!");
        }

        return value;
    }

    private Object parsePrimitive(Queue<String> parameters, String type, Function<String, Object> fun) {
        var value = getPrimitive(parameters);

        try {
            return fun.apply(value);
        } catch (Exception e) {
            throw new RuntimeException("The value '" + value + "' cannot be parsed to '" + type + "'!");
        }
    }

    @Override
    public Object[] getTestCase(String signatureMethod, Queue<String> arguments) {
        var parameters = getMethodParameters(signatureMethod);

        var testCase = new ArrayList<>();

        for (var parameter : parameters) {
            testCase.add(instantiate(getStructure(parameter), arguments));
        }

        return testCase.toArray();
    }

    private String[] getMethodParameters(String signature) {
        var signatureParsed = signature.replaceAll(" ", "");
        var matcher = Pattern.compile("\\((.*?)\\)").matcher(signatureParsed);

        if (matcher.find()) {
            return matcher.group(1).split(",");
        }

        throw new RuntimeException("Method parameters could not be extracted - '" + signatureParsed + "'!");
    }
}
