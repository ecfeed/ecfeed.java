package com.ecfeed.structure.setter;

import com.ecfeed.structure.dto.Structure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
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

    @Override
    public void activate(Structure structure, String signature) {
        var signatureParsed = getSignatureDefinition(signature);

        for (var constructor : structure.getConstructors().entrySet()) {
            if (constructor.getKey().equalsIgnoreCase(signatureParsed)) {
                if (structure.getActiveConstructor() != null && structure.getActiveConstructor() != constructor.getValue()) {
                    throw new RuntimeException("The redefinition of constructors is not supported. Affected structure: '" + structure.getNameSimple() + "'.");
                }

                structure.setActiveConstructor(constructor.getValue());
                break;
            }
        }

        if (structure.getActiveConstructor() == null) {
            throw new RuntimeException("The constructor for the structure '" + structure.getNameSimple() + "' is not defined in the source!");
        }

        structure.setActive(true);
    }

    private String getSignatureDefinition(String signature) {
        String[] argumentPairs = signature.split("[()]")[1].split(",");
        List<String> argumentTypes = new ArrayList<>();

        for (var i = 0 ; i < argumentPairs.length ; i++) {
            String[] argumentParsed = argumentPairs[i].trim().split(" ");
            argumentTypes.add(argumentParsed[0]);
        }

        var signatureParsed = signature;

        if (signatureParsed.contains(".")) {
            signatureParsed = signatureParsed.substring(signature.lastIndexOf(".") + 1);
        }

        signatureParsed = signatureParsed.substring(0, signatureParsed.lastIndexOf("("));
        signatureParsed = signatureParsed + "(" + String.join(",", argumentTypes) + ")";

        return signatureParsed;
    }
}
