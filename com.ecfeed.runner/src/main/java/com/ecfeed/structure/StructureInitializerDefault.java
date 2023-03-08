package com.ecfeed.structure;

import com.ecfeed.structure.dto.Structure;
import com.ecfeed.structure.setter.StructureSetterDefault;
import com.ecfeed.structure.setter.StructuresSetter;
import com.ecfeed.structure.setter.StructuresSetterDefault;

import java.util.HashSet;
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
    public void addSource(Class... source) {
        var structures = structuresSetter.parse(source);

        for (var structure : structures) {
            addSource(structure);
        }
    }

    @Override
    public void addSource(String... source) {
        var structures = structuresSetter.parse(source);

        for (var structure : structures) {
            addSource(structure);
        }
    }

    private void addSource(Structure source) {
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
    public Set<Structure> getStructures() {

        return structures;
    }

    @Override
    public Set<String> getNamesSimple() {

        return structures.stream().map(Structure::getNameSimple).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesQualified() {

        return structures.stream().map(Structure::getNameQualified).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("\n");

        structures.forEach(e -> builder.append(e.toString() + "\n"));

        return builder.toString();
    }

    @Override
    public Object initialize(String name, String constructor) {
        return null;
    }
}
