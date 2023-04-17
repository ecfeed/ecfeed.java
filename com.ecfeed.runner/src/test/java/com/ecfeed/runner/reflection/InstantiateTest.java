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

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class InstantiateTest {

    @Test
    @DisplayName("List structures from classes - Single source")
    void listStructuresClassSingleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source(Source.class);

        assertEquals(3, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from classes - Multiple sources")
    void listStructuresClassMultipleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

        assertEquals(10, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from classes - Multiple sources - Sequential")
    void listStructuresClassMultipleSequentialTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source(Source.class);
        initializer.source(SourceNested1.class);
        initializer.source(SourceNested2.class);
        initializer.source(SourceNested3.class);

        assertEquals(10, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from classes - Duplicate name")
    void listStructuresClassDuplicateNameTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.source(Source.class, SourceErroneous.class);
        });
    }

    @Test
    @DisplayName("List structures from classes - Correct name - Simple")
    void listStructuresClassNameSimpleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

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
        initializer.source(Source.class, SourceNested1.class, SourceNested2.class, SourceNested3.class);

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
        initializer.source("com.ecfeed.runner.reflection.source.correct.nested1");

        assertEquals(3, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Multiple sources")
    void listStructuresPackageMultipleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct.nested1", "com.ecfeed.runner.reflection.source.correct.nested2");

        assertEquals(6, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Multiple sources - Sequential")
    void listStructuresPackageMultipleSequentialTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct.nested1");
        initializer.source("com.ecfeed.runner.reflection.source.correct.nested2");

        assertEquals(6, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Nested sources")
    void listStructuresPackageNestedTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        assertEquals(10, initializer.getStructuresRaw().size());
    }

    @Test
    @DisplayName("List structures from packages - Duplicate name")
    void listStructuresPackageDuplicateNameTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.source("com.ecfeed.runner.reflection.source.correct", "com.ecfeed.runner.reflection.source.erroneous");
        });
    }

    @Test
    @DisplayName("List structures from packages - Wrong package")
    void listStructuresPackageWrongPackageTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        Assertions.assertThrows(RuntimeException.class, () -> {
            initializer.source("com.ecfeed.runner.reflection.source.empty");
        });
    }

    @Test
    @DisplayName("List structures from packages - Correct name - Simple")
    void listStructuresPackageNameSimpleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

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
        initializer.source("com.ecfeed.runner.reflection.source.correct");

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
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        assertEquals(0, initializer.getStructuresActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single")
    void activateStructuresSingleTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("Element1(int,double,String,Element2)");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single with spaces")
    void activateStructuresSingleWithSpacesTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate(" Element1 (int, double, String, Element2) ");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single with names")
    void activateStructuresSingleWithNamesTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate(" Element1(int arg1,double arg2,String arg3,Element2 arg4) ");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single with names and spaces")
    void activateStructuresSingleWithNamesAndSpacesTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate(" Element1 ( int arg1 , double arg2 , String arg3 , Element2 arg4) ");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Single with package name")
    void activateStructuresSingleWithPackageNameTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("test.package.Element1(int,double,String,Element2)");

        assertEquals(1, initializer.getStructuresActive().size());
        assertEquals(1, initializer.getNamesSimpleActive().size());
        assertEquals(1, initializer.getNamesQualifiedActive().size());
    }

    @Test
    @DisplayName("Activate structures - Erroneous structure")
    void activateStructuresErroneousStructureTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        assertThrows(RuntimeException.class, () -> {
            initializer.activate("test.package.Element100(int,double,String,Element2)");
        });
    }

    @Test
    @DisplayName("Activate structures - Erroneous constructor")
    void activateStructuresErroneousConstructorTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        assertThrows(RuntimeException.class, () -> {
            initializer.activate("test.package.Element1(int,double,String,Element2,int)");
        });
    }

    @Test
    @DisplayName("Activate structures - Redefinition - Same constructor")
    void activateStructuresRedefinitionSameConstructorTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("test.package.Element1(int,double,String,Element2)");
        initializer.activate("test.package.Element1(int,double,String,Element2)");
    }

    @Test
    @DisplayName("Activate structures - Redefinition - Different constructor")
    void activateStructuresRedefinitionDifferentConstructorTest() {
        StructureInitializer initializer = new StructureInitializerDefault();
        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("test.package.Element1(int,double,String,Element2)");

        assertThrows(RuntimeException.class, () -> {
            initializer.activate("test.package.Element1(int,double,string)");
        });
    }

//---------------------------------------------------------------------------------------

    @Test
    @DisplayName("Instantiate - Simple - Class")
    void instantiateSimpleClassTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element2(int,int,int)");

        Source.Element2 element = initializer.instantiate(Source.Element2.class, new LinkedList<>(Arrays.asList("1", "2", "3")));

        assertEquals(1, element.a);
        assertEquals(2, element.b);
        assertEquals(3, element.c);
    }

    @Test
    @DisplayName("Instantiate - Simple - Signature")
    void instantiateSimpleSignatureTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element2(int,int,int)");

        var element = (Source.Element2) initializer.instantiate("Element2", new LinkedList<>(Arrays.asList("1", "2", "3")));

        assertEquals(1, element.a);
        assertEquals(2, element.b);
        assertEquals(3, element.c);
    }

    @Test
    @DisplayName("Instantiate - Nested - Class")
    void instantiateNestedClassTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        var element = initializer.instantiate(Source.Element1.class, new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3")));

        assertEquals(1, element.a);
        assertEquals(2.5, element.b);
        assertEquals("test", element.c);
        assertNotNull(element.d);
        assertEquals(-1, element.d.a);
        assertEquals(-2, element.d.b);
        assertEquals(-3, element.d.c);
    }

    @Test
    @DisplayName("Instantiate - Nested - Signature")
    void instantiateNestedSignatureTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        var element = (Source.Element1) initializer.instantiate("Element1", new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3")));

        assertEquals(1, element.a);
        assertEquals(2.5, element.b);
        assertEquals("test", element.c);
        assertNotNull(element.d);
        assertEquals(-1, element.d.a);
        assertEquals(-2, element.d.b);
        assertEquals(-3, element.d.c);
    }

//---------------------------------------------------------------------------------------

    @Test
    @DisplayName("Instantiate - Nested - Not active")
    void instantiateNestedNotActiveTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");

        assertThrows(RuntimeException.class, () -> {
            initializer.instantiate(Source.Element1.class, new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3")));
        });
    }

    @Test
    @DisplayName("Instantiate - Nested - Wrong parameter type")
    void instantiateNestedWrongSourceTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        assertThrows(RuntimeException.class, () -> {
            initializer.instantiate(Source.Element1.class, new LinkedList<>(Arrays.asList("1.5", "2.5", "test", "-1", "-2", "-3")));
        });
    }

    @Test
    @DisplayName("Instantiate - Nested - Too short parameter list")
    void instantiateNestedTooShortParameterListTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        assertThrows(RuntimeException.class, () -> {
            initializer.instantiate(Source.Element1.class, new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2")));
        });
    }

    @Test
    @DisplayName("Instantiate - Nested - Too long parameter list")
    void instantiateNestedTooLongParameterListTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        assertThrows(RuntimeException.class, () -> {
            initializer.instantiate(Source.Element1.class, new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3", "-4")));
        });
    }

//---------------------------------------------------------------------------------------

    @Test
    @DisplayName("Get test case")
    void getTestCaseTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        var testCase = initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3", "ecFeed")));

        assertEquals(2, testCase.length);
        assertNotNull(testCase[0]);
        assertEquals(1, ((Source.Element1)testCase[0]).a);
        assertEquals(2.5, ((Source.Element1)testCase[0]).b);
        assertEquals("test", ((Source.Element1)testCase[0]).c);
        assertNotNull(((Source.Element1)testCase[0]).d);
        assertEquals(-1, ((Source.Element1)testCase[0]).d.a);
        assertEquals(-2, ((Source.Element1)testCase[0]).d.b);
        assertEquals(-3, ((Source.Element1)testCase[0]).d.c);
        assertEquals("ecFeed", testCase[1]);
    }

    @Test
    @DisplayName("Get test case - Not active")
    void getTestCaseNotActiveTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");

        assertThrows(RuntimeException.class, () -> {
            initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3", "ecFeed")));
        });
    }

    @Test
    @DisplayName("Get test case - Wrong parameter type")
    void getTestCaseWrongSourceTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        assertThrows(RuntimeException.class, () -> {
            initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("1.5", "2.5", "test", "-1", "-2", "-3", "ecFeed")));
        });
    }

    @Test
    @DisplayName("Get test case - Too short parameter list")
    void getTestCaseTooShortParameterListTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        assertThrows(RuntimeException.class, () -> {
            initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3")));
        });
    }

    @Test
    @DisplayName("Get test case - Too long parameter list")
    void getTestCaseTooLongParameterListTest() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");
        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        assertThrows(RuntimeException.class, () -> {
            initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("1", "2.5", "test", "-1", "-2", "-3", "ecFeed", "ecFeed")));
        });
    }

}
