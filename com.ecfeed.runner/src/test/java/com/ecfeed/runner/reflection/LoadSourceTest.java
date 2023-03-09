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

public class LoadSourceTest {

    @Test
    @DisplayName("List structures from classes - Single source")
    void listStructuresClassSingleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class);

        assertEquals(3, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from classes - Multiple sources")
    void listStructuresClassMultipleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertEquals(10, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from classes - Duplicate name")
    void listStructuresClassDuplicateNameTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.addSource(Source.class, SourceErroneous.class);
        });
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Simple")
    void listStructuresClassNameSimpleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertTrue(initializer.getNamesSimpleRaw().contains("Source"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("SourceNested1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element1Nested1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element2Nested1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("SourceNested2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element1Nested2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element2Nested2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("SourceNested3"));
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Qualified")
    void listStructuresClassNameQualifiedTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.Source"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.Source.Element1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.Source.Element2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element1Nested1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element2Nested1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element1Nested2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element2Nested2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested3.SourceNested3"));
    }

//---------------------------------------------------------------------------------------

    @Test
    @DisplayName("List structures from packages - Single source")
    void listStructuresPackageSingleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct.nested1");

        assertEquals(3, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Multiple sources")
    void listStructuresPackageMultipleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct.nested1", "com.ecfeed.runner.reflection.source.correct.nested2");

        assertEquals(6, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Nested sources")
    void listStructuresPackageNestedTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertEquals(10, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Duplicate name")
    void listStructuresPackageDuplicateNameTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.addSource("com.ecfeed.runner.reflection.source.correct", "com.ecfeed.runner.reflection.source.erroneous");
        });
    }

    @Test
    @DisplayName("List structures from packages - Wrong package")
    void listStructuresPackageWrongPackageTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.addSource("com.ecfeed.runner.reflection.source.empty");
        });
    }

    @Test
    @DisplayName("List structures from packages - Correct name - Simple")
    void listStructuresPackageNameSimpleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertTrue(initializer.getNamesSimpleRaw().contains("Source"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("SourceNested1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element1Nested1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element2Nested1"));
        assertTrue(initializer.getNamesSimpleRaw().contains("SourceNested2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element1Nested2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("Element2Nested2"));
        assertTrue(initializer.getNamesSimpleRaw().contains("SourceNested3"));
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Qualified")
    void listStructuresPackageNameQualifiedTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.Source"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.Source.Element1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.Source.Element2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element1Nested1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested1.SourceNested1.Element2Nested1"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element1Nested2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested2.SourceNested2.Element2Nested2"));
        assertTrue(initializer.getNamesQualifiedRaw().contains("com.ecfeed.runner.reflection.source.correct.nested3.SourceNested3"));
    }

//---------------------------------------------------------------------------------------

    @Test
    @DisplayName("Activate structures - Empty")
    void activateStructuresNoneTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertEquals(0, initializer.getStructuresActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single")
    void activateStructuresSingleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("Element1(int,double,String,Element2)");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single with spaces")
    void activateStructuresSingleWithSpacesTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        initializer.activate(" Element1 (int, double, String, Element2) ");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single with package name")
    void activateStructuresSingleWithPackageNameTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("test.package.Element1(int,double,String,Element2)");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Erroneous structure")
    void activateStructuresErroneousStructureTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertThrows(RuntimeException.class, () -> {
            initializer.activate("test.package.Element100(int,double,String,Element2)");
        });
    }

    @Test
    @DisplayName("Activate structures - Erroneous constructor")
    void activateStructuresErroneousConstructorTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        assertThrows(RuntimeException.class, () -> {
            initializer.activate("test.package.Element1(int,double,String,Element2,int)");
        });
    }

    @Test
    @DisplayName("Activate structures - Redefinition - Same constructor")
    void activateStructuresRedefinitionSameConstructorTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("test.package.Element1(int,double,String,Element2)");
        initializer.activate("test.package.Element1(int,double,String,Element2)");
    }

    @Test
    @DisplayName("Activate structures - Redefinition - Different constructor")
    void activateStructuresRedefinitionDifferentConstructorTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.addSource("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("test.package.Element1(int,double,String,Element2)");

        assertThrows(RuntimeException.class, () -> {
            initializer.activate("test.package.Element1(int,int,int)");
        });
    }
}
