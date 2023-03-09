package com.ecfeed.structure.setter;

import com.ecfeed.structure.dto.Structure;

import java.util.List;
import java.util.Set;

public interface StructuresSetter {

    List<Structure> parse(Class... source);

    List<Structure> parse(String... source);

    void activate(Set<Structure> structures, String... signature);
}
