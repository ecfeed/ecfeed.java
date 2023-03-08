package com.ecfeed.structure.dto;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;

public class Structure {

    private Class source;
    private String nameSimple;
    private String nameQualified;
    private Map<String, Constructor> constructors;

    public Class getSource() {
        return source;
    }

    public void setSource(Class source) {
        this.source = source;
    }

    public String getNameSimple() {
        return nameSimple;
    }

    public void setNameSimple(String nameSimple) {
        this.nameSimple = nameSimple;
    }

    public String getNameQualified() {
        return nameQualified;
    }

    public void setNameQualified(String nameQualified) {
        this.nameQualified = nameQualified;
    }

    public Map<String, Constructor> getConstructors() {
        return constructors;
    }

    public void setConstructors(Map<String, Constructor> constructors) {
        this.constructors = constructors;
    }

    @Override
    public String toString() {

        return getNameSimple();
//        var builder = new StringBuilder();
//
//        builder.append(getNameSimple() + "\n");
//
//        for (var constructor : getConstructors().entrySet()) {
//            builder.append("- " + constructor.getKey() + "\n");
//        }
//
//        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Structure structure = (Structure) o;
        return nameSimple.equals(structure.nameSimple);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameSimple);
    }
}
