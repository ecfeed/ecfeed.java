# Integration with Java

## Introduction

The following tutorial is an introduction to the Java runner. Note, that it does not cover the ecFeed basics. Therefore, if you want to learn how to create a sample model and generate a personal keystore, visit the tutorial section on our [webpage](https://ecfeed.com/tutorials).  

Prerequisites:
- Install Java JDK, e.g. [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/).
- Download an IDE, e.g. [VSCode](https://code.visualstudio.com/), [IntelliJ](https://www.jetbrains.com/idea/download/), [Eclipse](https://www.eclipse.org/downloads/).
- Create a test model on the ecFeed webpage (or use the default one).
- Generate a personal keystore named 'security.p12' and put it in the \~/.ecfeed/ directory (Linux users) or in the \~/ecfeed/ directory (Windows users).  

For complete documentation check the source directly at [GitHub](https://github.com/ecfeed/ecfeed.java).  

The ecFeed library can be found in the [Maven Repository](https://mvnrepository.com/artifact/com.ecfeed/ecfeed.java).  

## Examples

Methods, used in the tutorial, are a part of the welcome model, created during the registration process at the 'ecfeed.com' webpage. If the model is missing (e.g. it has been deleted by the user), it can be downloaded from [here](https://s3-eu-west-1.amazonaws.com/resources.ecfeed.com/repo/tutorial/Welcome.ect).  

```java
import com.ecfeed.TestProvider;
import com.ecfeed.TypeExport;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX");            // The model ID.
        
        for (String chunk : testProvider.exportNWise("QuickStart.test", TypeExport.CSV)) {      // The name of the method.
            System.out.println(chunk);
        }
    }
}
```

Do not hesitate to experiment with the code and modify the welcome model. It can be recreated easily and there is no better way to learn than hands-on exercises.  

However, have in mind that the ID of each model (including the welcome model) is unique. If you want to copy and paste the example, be sure to update it accordingly.

## JUnit5

The ecFeed library can be used to create test cases for JUnit5, which is one of the most common testing frameworks for Java. It is possible, because generation methods return the 'Iterable<Object[]>' interface, which can be directly used as the data source.  

```java
public class JUnit5Test {
    
    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX");
        return testProvider.generateNWise("QuickStart.test");
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(int arg1, int arg2, int arg3) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
    }

}
```

It is possible to use enums as arguments. To do so, they must be defined in the project.  

```java
public class JUnit5Test {
    
    enum Gender {
        MALE, FEMALE
    }

    enum ID {
        PASSPORT, DRIVERS_LICENSE, PERSONAL_ID
    }

    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX");
        return testProvider.generateNWise("com.example.test.LoanDecisionTest2.generateCustomerData");
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

}
```

If you want to use classes (defined as structures in the model) you must manually create a constructor for each one of them. 
Classes are retrieved from the code using reflection and instantiated using values received from the server. 
Note, that if a structure is directly linked to a global structure (it does not contain any additional fields), only the definition of the global structure is needed, regardless of the name of the linked structure.  

```java
public class Source {

    public static class Element1 {

        public Element1(int a, double b, String c, Element2 d) {
        }
    }

    public static class Element2 {

        public Element2(int a, int b, int c) {
        }
    }
}
```

```java
public class JUnitStructure {

    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX");
        return testProvider.generateNWise("com.example.test.structure", ParamsNWise.create().typesDefinitionsSource(Source.class));
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void genNWise(Source.Element1 a, int b) {
        System.out.println("a = " + a + ", b = " + b );
    }
}
```

## Feedback

To send feedback, you need to have a BASIC account type or be a member of a TEAM.

A feedback example looks as follows:

```java
static final String MODEL = "XXXX-XXXX-XXXX-XXXX-XXXX";
static final String METHOD = "QuickStart.test";

static Iterable<Object[]> feedback() {
    return TestProvider.create(MODEL).generateRandom(METHOD, ParamsRandom.create().feedback().length(1));
}
        
@ParameterizedTest
@MethodSource("feedback")
void feedbackTest(int arg1, int arg2, int arg3, TestHandle testHandle) {
    Assertions.assertTrue(arg1 < 2, () -> testHandle.addFeedback(false, "Failed - arg1 < 2"));
    testHandle.addFeedback(true, "OK");
}
```

To the generation method an additional parameter, i.e. 'TestHandle testHandle', must be added. The class contains an overloaded method 'addFeedback'. The required argument denotes the result of the test, everything else is optional.

```java
public String addFeedback(boolean status, int duration, String comment, Map<String, String> custom)
```

- *status* - The result of the test. This is the only required field.
- *duration* - The optional execution time in milliseconds.
- *comment* - The optional description of the execution.
- *custom* -  The optional map of custom key-value pairs. 
  
Note, that each test must return a feedback, regardless whether it has passed or failed. In each test, only the first invocation of the 'addFeedback' method takes effect. All subsequent invocations are neglected.  

Generated feedback can be analyzed on the 'ecfeed.com' webpage.  

# TestProvider class API

The library provides connectivity with the ecFeed test generation service using the 'TestProvider' class. It requires the model ID, the keystore location, the keystore password, and the generator web address.  

## Constructor

The 'TestProvider' constructor consists of one required and three optional parameters which can be provided in the form of 'Map<String, String>'. If they are non-existent, default values are used (which, for the vast majority of cases, are sufficient).  

- *model* - The model ID. It is a 20-digit number (grouped by 4) that can be found in the 'My projects' page at 'ecfeed.com'. It can be also found in the URL of the model editor page.
```java
TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX");
```
- *keyStorePath* - The path to the keystore downloaded from the 'ecfeed.com' webpage ('Settings' -> 'Security'). The keystore contains the user certificate which is needed to authenticate the user at the generator service. By default, the constructor looks for the keystore in \~/.ecfeed/security.p12, except for Windows, where the default path is \~/ecfeed/security.p12.
```java
Map<String, String> configProvider = new HashMap<>();
configProvider.put("keyStorePath", "testKeyStorePath");
TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX", configProvider);
```
- *keyStorePassword* - The password to the keystore. The default value is 'changeit'.  
```java
Map<String, String> configProvider = new HashMap<>();
configProvider.put("keyStorePassword", "testKeyStorePassword");
TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX", configProvider);
```
- *generatorAddress* - The URL of the ecfeed generator service. By default, it is 'gen.ecfeed.com'.
```java
Map<String, String> configProvider = new HashMap<>();
configProvider.put("generatorAddress", "testGeneratorAddress");
TestProvider testProvider = TestProvider.create("XXXX-XXXX-XXXX-XXXX-XXXX", configProvider);
```

## Generator calls

'TestProvider' can invoke five methods to access the ecFeed generator service. They produce data parsed to 'Object[]'. Additional parameters can be included in a configuration object (or a map).

### public Iterable<Object[]> generateNWise(String method, Param.ParamsNWise config)

Generate test cases using the NWise algorithm.  

Arguments:
- *method (required)* - The full name of the method that will be used for generation (including the package). If the method is not overloaded, its parameters are not required.
- *n* - The 'N' value required in the NWise algorithm. The default is 2 (pairwise).
- *coverage* - The percentage of N-tuples that the generator will try to cover. The default is 100.
- *choices* - A map in which keys are names of method parameters. Their values define a list of choices that should be used during the generation process. If a key is skipped, all choices are used.
- *constraints* - An array of constraint names used in the generation process. If not provided, all constraints are used. Additionally, two string values can be used instead, i.e. "ALL", "NONE".
- *feedback* - A flag denoting whether feedback should be sent beck to the generator. By default, this functionality is switched off.
- *label* - An additional label associated with feedback.
- *custom* - An additional map ('Map<String, String>') with custom elements associated with feedback.
- *typesDefinitionsSource* - Classes or packages where custom definitions of the model structures can be found. Note, that nested packages are included as well.

```java
String[] constraints = new String[]{ "constraint" };

Map<String, String[]> choices = new HashMap<>();
choices.put("arg1", new String[]{ "choice1", "choice2" });

Map<String, String> custom = new HashMap<>();
custom.put("key1", "value1");

Param.ParamsNWise config = new Param.ParamsNWise()
        .constraints(constraints)
        .choices(choices)
        .coverage(100)
        .n(3)
        .feedback()
        .label("label")
        .typesDefinitionsSource(Source1.class, Source2.class)
        .typesDefinitionsSource("com.example.source.a", "com.example.source.b")
        .custom(custom);

testProvider.generateNWise("QuickStart.test", config)
```

Also, additional parameters can be provided using a map.  

```java
Map<String, Object> config = new HashMap<>();

config.put("n", "2");
config.put("coverage", "100");

String[] constraints = new String[]{ "constraint" };
config.put("constraints", constraints);

Map<String, String[]> choices = new HashMap<>();
choices.put("arg1", new String[]{ "choice1", "choice2" });
config.put("choices", choices);

config.put("feedback", "true");
config.put("label", "label");

Map<String, String> custom = new HashMap<>();
custom.put("key1", "value1");
config.put("custom", custom);

testProvider.generateNWise("QuickStart.test", config)
```

If the configuration object/map is not provided, default values are used.  

### public Iterable<Object[]> generatePairwise(String method, Param.ParamsPairwise config)

Calls the '2-wise' generation procedure. For people that like being explicit. Apart from 'n' (which is always 2 and cannot be changed), the method accepts the same arguments as 'generateNWise'.  

### public Iterable<Object[]> generateCartesian(String method, Param.ParamsCartesian config)

Generate test cases using the Cartesian product.

Arguments:
- *method (required)* - See 'generateNWise'.
- *choices* - See 'generateNWise'.
- *constraints* - See 'generateNWise'.
- *feedback* - See 'generateNWise'.
- *label* - See 'generateNWise'.
- *custom* - See 'generateNWise'.
- *typesDefinitionsSource* - See 'generateNWise'.

### public Iterable<Object[]> generateRandom(String method, Param.ParamsRandom config)

Generate randomized test cases.

Arguments:
- *method (required)* - See 'generateNWise'.
- *length* - The number of tests to be generated. The default value is 1.
- *duplicates* - If two identical tests are allowed to be generated. If set to 'false', the generator will stop after creating all allowed combinations. The default value is 'true'.
- *adaptive* - If set to true, the generator will try to provide tests that are farthest (in the means of the Hamming distance) from the ones already generated. The default value is 'false'.
- *choices* - See 'generateNWise'.
- *constraints* - See 'generateNWise'.
- *feedback* - See 'generateNWise'.
- *label* - See 'generateNWise'.
- *custom* - See 'generateNWise'.
- *typesDefinitionsSource* - See 'generateNWise'.

### public Iterable<Object[]> generateStatic(String method, Param.ParamsStatic config)

Download generated test cases (do not use the generator).  

Arguments:
- *method (required)* - See 'generateNWise'.
- *testSuites* - An array of test suite names to be downloaded. Additionally, a string value can be used instead, i.e. "ALL".
- *feedback* - See 'generateNWise'.
- *label* - See 'generateNWise'.
- *custom* - See 'generateNWise'.
- *typesDefinitionsSource* - See 'generateNWise'.

## Export calls

Those methods look similarly to 'generate' methods. However, they return the 'Iterable<String>' interface, do not parse the data, and generate the output using templates. For this reason, they require one more argument, namely 'template'. The predefined values are: 'TypeExport.JSON', 'TypeExport.XML', 'TypeExport.Gherkin', 'TypeExport.CSV', 'TypeExport.Raw'. 

```java
public Iterable<String> exportNWise(String method, TypeExport typeExport, Param.ParamsNWise config);
public Iterable<String> exportPairwise(String method, TypeExport typeExport, Param.ParamsPairwise config);
public Iterable<String> exportCartesian(String method, TypeExport typeExport, Param.ParamsCartesian config);
public Iterable<String> exportRandom(String method, TypeExport typeExport, Param.ParamsRandom config);
public Iterable<String> exportStatic(String method, TypeExport typeExport, Param.ParamsStatic config);
```

## Other methods

The following section describes supplementary methods.

### public void validateConnection()

Verifies if the connection settings (including the keystore) are correct. If something is wrong, an exception is thrown. 

### public List<String> getMethodNames(String methodName)

Gets the names of the method parameters in the on-line model.

### public List<String> getMethodTypes(String methodName)

Gets the types of the method parameters in the on-line model.