# Integration with Java

## Introduction

The following tutorial is an introduction to the Java runner. Note, that it does not cover the ecFeed basics. Therefore, if you want to learn how to create a sample model and generate a personal keystore, visit the tutorial section on our [webpage](https://ecfeed.com/tutorials).  

Prerequisites:
- Install Java JDK, e.g. [OpenJDK 11](https://openjdk.java.net/projects/jdk/11/).
- Install an IDE, e.g. [IntelliJ](https://www.jetbrains.com/idea/download/), [Eclipse](https://www.eclipse.org/downloads/).
- Create a test model on the ecFeed webpage.
- Generate a personal keystore named 'security.p12' and put it in the \~/.ecfeed/ directory (Linux users) or in the \~/ecfeed/ directory (Windows users).  

For the complete documentation check the source directly at [GitHub](https://github.com/ecfeed/ecfeed.java).  

The ecFeed library can be found online in the [Maven Repository](https://mvnrepository.com/artifact/com.ecfeed/ecfeed.java).  

## Examples

Methods, used in the tutorial, are a part of the welcome model, created during the registration process at the 'ecfeed.com' webpage. If the model is missing (e.g. it has been deleted by the user), it can be downloaded (and then imported) from [here](https://s3-eu-west-1.amazonaws.com/resources.ecfeed.com/repo/tutorial/Welcome.ect).  

```java
import com.ecfeed.TestProvider;
import com.ecfeed.TypeExport;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL");            // The model ID.
        
        for (String chunk : testProvider.exportNWise("QuickStart.test", TypeExport.CSV)) {      // The method name.
            System.out.println(chunk);
        }
    }
}
```

Do not hesitate to experiment with the code and modify the welcome mode. It can be recreated easily and there is no better way to learn than hands-on exercises.  

However, have in mind that the ID of each model (including the welcome model) is unique. If you want to copy and paste the above example, be sure to update it accordingly.

## JUnit5

The ecFeed library can be used to create test cases for JUnit, which is one of the mose common testing frameworks for Java. It is possible, because generation methods return the 'Iterable<Object[]>' interface, which can be directly used as the data source.  

```java
public class JUnit5Test {
    
    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL");
        return testProvider.generateNWise("QuickStart.test");
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(int arg1, int arg2, int arg3) {
        System.out.println("arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
    }

}
```

It is also possible to use enums as arguments. To do so, they must be defined (and visible) somewhere in the project.  

```java
public class JUnit5Test {
    
 enum Gender {
        MALE, FEMALE
    }

    enum ID {
        PASSPORT, DRIVERS_LICENSE, PERSONAL_ID
    }

    static Iterable<Object[]> testProviderNWise() {
        TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL");
        return testProvider.generateNWise("com.example.test.LoanDecisionTest2.generateCustomerData");
    }

    @ParameterizedTest
    @MethodSource("testProviderNWise")
    void testProviderNWise(String name, String firstName, Gender gender, int age, String id, ID type) {
        System.out.println("name = " + name + ", firstName = " + firstName + ", gender = " + gender + ", age = " + age + ", id = " + id + ", type = " + type);
    }

}
```

# TestProvider class API

The library provides connectivity with the ecFeed test generation service using the 'TestProvider' class. It requires the model ID, the keystore location, the keystore password, and the generator service address.  

## Constructor

The 'TestProvider' constructor takes one required and three optional parameters which can be provided in the form of a 'Map<String, String>'. If they are not provided, default values are used (which, for the vast majority of cases, are sufficient).  

- *model* - The model ID. It is a 20 digit number (grouped by 4) that can be found in the 'My projects' page at 'ecfeed.com'. It can be also found in the URL of the model editor page.
```java
TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL");
```
- *keyStorePath* - The path to the keystore downloaded from the 'ecfeed.com' webpage ('Settings' -> 'Security'). The keystore contains the user certificate which is needed to authenticate the user at the generator service. Be default, the constructor looks for the keystore in \~/.ecfeed/security.p12, except for Windows, where the default path is \~/ecfeed/security.p12.
```java
Map<String, String> configProvider = new HashMap<>();
configProvider.put("keyStorePath", "testKeyStorePath");
TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL", configProvider);
```
- *keyStorePassword* - The password for the keystore. The default value is 'changeit' which is the password used to encrypt the keystore downloaded form the 'ecfeed.com' page.
```java
Map<String, String> configProvider = new HashMap<>();
configProvider.put("keyStorePassword", "testKeyStorePassword");
TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL", configProvider);
```
- *generatorAddress* - The URL of the ecfeed generator service. By default, it is 'gen.ecfeed.com'.
```java
Map<String, String> configProvider = new HashMap<>();
configProvider.put("generatorAddress", "testGeneratorAddress");
TestProvider testProvider = TestProvider.create("GA1C-N74Z-HKAT-6FMS-35EL", configProvider);
```

## Generator calls

'TestProvider' can invoke five methods to access the ecFeed generator service. They produce data parsed to 'Object[]'. Additional parameters can be included in a configuration object (or a map).

### public Iterable<Object[]> generateNWise(String method, Param.ParamsNWise config)

Generate test cases using the NWise algorithm.  

Arguments:
- *method (required)* - The full name of the method that will be used for generation (including the package). If the method is not overloaded, its parameters are not required.
- *n* - The 'N' value required in the NWise algorithm. The default is 2 (pairwise).
- *coverage* - The percentage of N-tuples that the generator will try to cover. The default is 100.
- *choices* - A map in which keys are names of method parameters. Their values define a list of choices that should be used during the generation process. If an argument is skipped, all choices are used.
- *constraints* - An array of constraints used for the generation. If not provided, all constraints are used. Additionally, two String values can be used instead, i.e. "ALL", "NONE".

```java
String[] constraints = new String[]{ "constraint" };

Map<String, String[]> choices = new HashMap<>();
choices.put("arg1", new String[]{ "choice1", "choice2" });

Param.ParamsNWise config = new Param.ParamsNWise()
        .constraints(constraints)
        .choices(choices)
        .coverage(100)
        .n(3);

testProvider.generateNWise("QuickStart.test", config)
```

Also, additional parameters can be passed using a map.  

```java
Map<String, Object> config = new HashMap<>();

config.put("n", "2");
config.put("coverage", "100");

String[] constraints = new String[]{ "constraint" };
config.put("constraints", constraints);

Map<String, String[]> choices = new HashMap<>();
choices.put("arg1", new String[]{ "choice1", "choice2" });
config.put("choices", choices);

testProvider.generateNWise("QuickStart.test", config)
```

If the configuration object/map is not provided, default values are used.  

### public Iterable<Object[]> generatePairwise(String method, Param.ParamsPairwise config)

Calls n-wise with n=2. For people that like being explicit. Uses the same arguments as 'generateNWise' excluding 'n'.  

### public Iterable<Object[]> generateCartesian(String method, Param.ParamsCartesian config)

Generate test cases using the Cartesian product.

Arguments:
- *method (required)* - See 'generateNWise'.
- *choices* - See 'generateNWise'.
- *constraints* - See 'generateNWise'.

### public Iterable<Object[]> generateRandom(String method, Param.ParamsRandom config)

Generate randomized test cases.

Arguments:
- *method (required)* - See 'generateNWise'.
- *length* - The number of tests to be generated. The default is 1.
- *duplicates* - If two identical tests are allowed to be generated. If set to 'false', the generator will stop after creating all allowed combinations. The default is 'true'.
- *adaptive* - If set to true, the generator will try to provide tests that are farthest (in the means of the Hamming distance) from the ones already generated. The default is 'false'.
- *choices* - See 'generateNWise'.
- *constraints* - See 'generateNWise'.

### public Iterable<Object[]> generateStatic(String method, Param.ParamsStatic config)

Download generated test cases (do not use the generator).  

Arguments:
- *method (required)* - See 'generateNWise'.
- *testSuites* - An array of test case names to be downloaded. Additionally, one string value can be used instead, i.e. "ALL".

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

The following section describes non-crucial methods.

### public void validateConnection()

Verifies if the connection settings (including the keystore) are correct. If something is wrong, an exception is thrown. 

### public List<String> getMethodNames(String methodName)

Gets the names of the method parameters in the on-line model.

### public List<String> getMethodTypes(String methodName)

Gets the types of the method parameters in the on-line model.
