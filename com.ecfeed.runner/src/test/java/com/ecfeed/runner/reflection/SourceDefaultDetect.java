package com.ecfeed.runner.reflection;

import com.ecfeed.runner.reflection.source.correct.Source;
import com.ecfeed.runner.reflection.source.erroneous.SourceErroneous;
import com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1;
import com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2;
import com.ecfeed.runner.reflection.source.correct.nested3.SourceNested3;
import com.ecfeed.structure.StructureInitializer;
import com.ecfeed.structure.StructureInitializerDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SourceDefaultDetect {

    @Test
    @DisplayName("List structures from classes - Single source")
    void listStructuresClassSingle() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class);

        assertEquals(3, initializer.getStructures().size());
    }

    @Test
    @DisplayName("List structures from classes - Multiple sources")
    void listStructuresClassMultiple() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertEquals(10, initializer.getStructures().size());
    }

    @Test
    @DisplayName("List structures from classes - Duplicate name")
    void listStructuresClassDuplicateName() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.addSource(Source.class, SourceErroneous.class);
        });
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Simple")
    void listStructuresClassNameSimple() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertTrue(initializer.getNamesSimple().contains("Source"));
        assertTrue(initializer.getNamesSimple().contains("Element1"));
        assertTrue(initializer.getNamesSimple().contains("Element2"));
        assertTrue(initializer.getNamesSimple().contains("SourceNested1"));
        assertTrue(initializer.getNamesSimple().contains("Element1Nested1"));
        assertTrue(initializer.getNamesSimple().contains("Element2Nested1"));
        assertTrue(initializer.getNamesSimple().contains("SourceNested2"));
        assertTrue(initializer.getNamesSimple().contains("Element1Nested2"));
        assertTrue(initializer.getNamesSimple().contains("Element2Nested2"));
        assertTrue(initializer.getNamesSimple().contains("SourceNested3"));
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Qualified")
    void listStructuresClassNameQualified() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.Source"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.Source.Element1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.Source.Element2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element1Nested1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element2Nested1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element1Nested2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element2Nested2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested3.SourceNested3"));
    }

//---------------------------------------------------------------------------------------

    @Test
    @DisplayName("List structures from packages - Single source")
    void listStructuresPackageSingle() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct.nested1");

        assertEquals(3, initializer.getStructures().size());
    }

    @Test
    @DisplayName("List structures from packages - Multiple sources")
    void listStructuresPackageMultiple() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct.nested1", "com.ecfeed.runner.reflection.source.correct.nested2");

        assertEquals(6, initializer.getStructures().size());
    }

    @Test
    @DisplayName("List structures from packages - Nested sources")
    void listStructuresPackageNested() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertEquals(10, initializer.getStructures().size());
    }

    @Test
    @DisplayName("List structures from packages - Duplicate name")
    void listStructuresPackageDuplicateName() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.addSource("com.ecfeed.runner.reflection.source.correct", "com.ecfeed.runner.reflection.source.erroneous");
        });
    }

    @Test
    @DisplayName("List structures from packages - Wrong package")
    void listStructuresPackageWrongPackage() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.addSource("com.ecfeed.runner.reflection.source.empty");
        });
    }

    @Test
    @DisplayName("List structures from packages - Correct name - Simple")
    void listStructuresPackageNameSimple() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertTrue(initializer.getNamesSimple().contains("Source"));
        assertTrue(initializer.getNamesSimple().contains("Element1"));
        assertTrue(initializer.getNamesSimple().contains("Element2"));
        assertTrue(initializer.getNamesSimple().contains("SourceNested1"));
        assertTrue(initializer.getNamesSimple().contains("Element1Nested1"));
        assertTrue(initializer.getNamesSimple().contains("Element2Nested1"));
        assertTrue(initializer.getNamesSimple().contains("SourceNested2"));
        assertTrue(initializer.getNamesSimple().contains("Element1Nested2"));
        assertTrue(initializer.getNamesSimple().contains("Element2Nested2"));
        assertTrue(initializer.getNamesSimple().contains("SourceNested3"));
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Qualified")
    void listStructuresPackageNameQualified() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.Source"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.Source.Element1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.Source.Element2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element1Nested1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element2Nested1"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element1Nested2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element2Nested2"));
        assertTrue(initializer.getNamesQualified().contains("com.ecfeed.runner.reflection.source.correct.nested3.SourceNested3"));
    }
}
