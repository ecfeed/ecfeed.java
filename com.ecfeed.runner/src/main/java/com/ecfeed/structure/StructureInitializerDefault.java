package com.ecfeed.structure;

import com.ecfeed.structure.dto.Structure;
import com.ecfeed.structure.setter.StructureSetterDefault;
import com.ecfeed.structure.setter.StructuresSetter;
import com.ecfeed.structure.setter.StructuresSetterDefault;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
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
    public Object instantiate(String signatureStructure, Queue<String> parameters) {
        Structure structureCandidate = null;

        for (var structure : structures) {
            if (structure.getNameSimple().equalsIgnoreCase(signatureStructure)) {
                structureCandidate = structure;
                break;
            }
        }

        if (structureCandidate == null) {
            throw new RuntimeException("The required structure '" + signatureStructure + "' could not be found in the source");
        }

        if (!structureCandidate.isActive()) {
            throw new RuntimeException("The required structure '" + signatureStructure + "' has not been activated'");
        }

        var constructorParameters = new LinkedList<>();

        for (var type : structureCandidate.getActiveConstructor().getParameterTypes()) {
            constructorParameters.add(instantiateType(getTypeName(type.getCanonicalName()), parameters));
        }

        try {
            return structureCandidate.getActiveConstructor().newInstance(constructorParameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException("The structure '" + signatureStructure + "' could not be instantiated!");
        }
    }

    private <T> T cast(Object object, Class<T> type) {
        return type.cast(object);
    }

    private Object instantiateType(String type, Queue<String> parameters) {

        switch (type) {
            case "byte": return Byte.parseByte(parameters.poll());
            case "short": return Short.parseShort(parameters.poll());
            case "int": return Integer.parseInt(parameters.poll());
            case "long": return Long.parseLong(parameters.poll());
            case "float": return Float.parseFloat(parameters.poll());
            case "double": return Double.parseDouble(parameters.poll());
            case "boolean": return Boolean.parseBoolean(parameters.poll());
            case "char": return parameters.poll().substring(0, 1);
            case "String": return parameters.poll();
            default: return instantiate(type, parameters);
        }
    }

    private String getTypeName(String signature) {
        var signatureParsed = signature.replaceAll(" ", "");

        if (signatureParsed.contains(".")) {
            return signatureParsed.substring(signature.lastIndexOf(".") + 1);
        }

        return signatureParsed;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("\n");

        structures.forEach(e -> builder.append(e.toString() + "\n"));

        return builder.toString();
    }
}
