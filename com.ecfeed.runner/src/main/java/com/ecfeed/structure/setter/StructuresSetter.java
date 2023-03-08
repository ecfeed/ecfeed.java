package com.ecfeed.structure.setter;

import com.ecfeed.structure.dto.Structure;

import java.util.List;

public interface StructuresSetter {

    List<Structure> parse(Class... source);

    List<Structure> parse(String... source);
}
