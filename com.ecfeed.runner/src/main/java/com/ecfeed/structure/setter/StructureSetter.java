package com.ecfeed.structure.setter;

import com.ecfeed.structure.dto.Structure;

import java.lang.reflect.Constructor;

public interface StructureSetter {

    Structure parse(Class source);

    void activate(Structure structure, String signature);
}
