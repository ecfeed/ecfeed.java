package com.ecfeed;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.connection.ConnectionHandler;
import com.ecfeed.session.dto.DataSessionConnection;
import com.ecfeed.session.dto.DataSession;
import com.ecfeed.params.*;
import com.ecfeed.queue.IterableTestQueue;
import com.ecfeed.type.TypeExport;
import com.ecfeed.type.TypeGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class provides connectivity with the ecFeed test generation service.
 */
public class TestProvider {

    private static int ITERATOR_TIMEOUT = 1000;
    private static int ITERATOR_TIMEOUT_STEP = 100;

    private String model;
    private DataSessionConnection dataSessionConnection;
    private ConnectionHandler connectionHandler;

    private final List<Exception> exceptions = new ArrayList<>();

    private TestProvider(String model, Map<String, String> config) {

        connectionHandler = Factory.getConnectionHandler();

        setup(model, config);
    }

    /**
     * Creates a TestProvider object.
     *
     * @param model     the UUID of the ecFeed model
     * @return          an instance of the TestProvider class
     */
    public static TestProvider create(String model) {

        return new TestProvider(model, new HashMap<>());
    }

    /**
     * Creates a TestProvider object.
     *
     * @param model     the UUID of the ecFeed model
     * @param config    optional configuration parameters
     * @return          an instance of the TestProvider class
     */
    public static TestProvider create(String model, Map<String, String> config) {

        return new TestProvider(model, config);
    }

    /**
     * Gets the UUID of the ecFeed model.
     *
     * @return  the UUID of the ecFeed model
     */
    public String getModel() {

        return model;
    }

    /**
     * Gets the generator address.
     *
     * @return  the generator address
     */
    public String getAddress() {

        return this.dataSessionConnection.getHttpAddress();
    }

    /**
     * Gets the key store path.
     *
     * @return  the key store path
     */
    public Path getKeyStorePath() {

        return this.dataSessionConnection.getKeyStorePath();
    }

    /**
     * Generates test cases and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param generator     the generator type
     * @param typeExport    the export data format
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> export(String method, TypeGenerator generator, TypeExport typeExport, Map<String, Object> properties) {
        String template;

        ConfigDefault.processUserParameters(properties);
        ConfigDefault.validateUserParameters(properties);

        if (typeExport == TypeExport.Custom) {
            if (!properties.containsKey(ConfigDefault.Key.parDataTemplate)) {
                throw new IllegalArgumentException("For the 'custom' template type, the 'template' property must be defined");
            }

            template = properties.get(ConfigDefault.Key.parDataTemplate).toString();

            properties.remove(ConfigDefault.Key.parDataTemplate);
        } else if (typeExport == TypeExport.RFC_4180) {
            template = "RFC 4180\nDelimiter:,\nExplicit:false\nNested:false";
        } else if (typeExport == TypeExport.RFC_4627) {
            template = "RFC 4627\nIndent:2\nExplicit:false\nNested:false";
        } else {
            template = typeExport.toString();
        }

        var iterator = Factory.getIterableTestQueueExport();

        DataSession dataSession = Factory.getDataSession(this.dataSessionConnection, this.model, method, generator);
        dataSession.setOptionsGenerator(properties);
        dataSession.setExportTemplate(template);

        new Thread(() -> {
            try {
                connectionHandler.processChunkStream(iterator, connectionHandler.getChunkStreamForTestData(dataSession));
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                iterator.terminate();
            }
        }).start();

        validate(iterator);

        return iterator;
    }

    /**
     * Generates test cases using the n-wise algorithm and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportNWise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return export(method, TypeGenerator.NWise, typeExport, updatedProperties);
    }

    /**
     * Generates test cases using the n-wise algorithm and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportNWise(String method, TypeExport typeExport, ParamsNWise properties) {

        return exportNWise(method, typeExport, properties.getParamsMap());
    }

    /**
     * Generates test cases using the n-wise algorithm and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportNWise(String method, TypeExport typeExport) {

        return exportNWise(method, typeExport, ParamsNWise.create());
    }

    /**
     * Generates test cases using the pairwise algorithm and parses them to the provided text data format.
     * Note, that pairwise is specific type of the n-wise algorithm where n=2.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportPairwise(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return export(method, TypeGenerator.NWise, typeExport, updatedProperties);
    }

    /**
     * Generates test cases using the pairwise algorithm and parses them to the provided text data format.
     * Note, that pairwise is specific type of the n-wise algorithm where n=2.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportPairwise(String method, TypeExport typeExport, ParamsPairwise properties) {

        return exportPairwise(method, typeExport, properties.getParamsMap());
    }

    /**
     * Generates test cases using the pairwise algorithm and parses them to the provided text data format.
     * Note, that pairwise is specific type of the n-wise algorithm where n=2.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportPairwise(String method, TypeExport typeExport) {

        return exportPairwise(method, typeExport, ParamsPairwise.create());
    }

    /**
     * Generates test cases using the cartesian product algorithm and parses them to the provided text data format.
     * Note, this technique can generate a tremendous number of test cases, and therefore, is not recommended.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportCartesian(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, TypeGenerator.Cartesian, typeExport, updatedProperties);
    }

    /**
     * Generates test cases using the cartesian product algorithm and parses them to the provided text data format.
     * Note, this technique can generate a tremendous number of test cases, and therefore, is not recommended.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportCartesian(String method, TypeExport typeExport, ParamsCartesian properties) {

        return exportCartesian(method, typeExport, properties.getParamsMap());
    }

    /**
     * Generates test cases using the cartesian product algorithm and parses them to the provided text data format.
     * Note, this technique can generate a tremendous number of test cases, and therefore, is not recommended.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportCartesian(String method, TypeExport typeExport) {

        return exportCartesian(method, typeExport, ParamsCartesian.create());
    }

    /**
     * Generates test cases using the random algorithm and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportRandom(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parLength, ConfigDefault.Value.parLength + "");
        addProperty(updatedProperties, ConfigDefault.Key.parAdaptive, ConfigDefault.Value.parAdaptive + "");
        addProperty(updatedProperties, ConfigDefault.Key.parDuplicates, ConfigDefault.Value.parDuplicates + "");

        return export(method, TypeGenerator.Random, typeExport, updatedProperties);
    }

    /**
     * Generates test cases using the random algorithm and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportRandom(String method, TypeExport typeExport, ParamsRandom properties) {

        return exportRandom(method, typeExport, properties.getParamsMap());
    }

    /**
     * Generates test cases using the random algorithm and parses them to the provided text data format.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<String> exportRandom(String method, TypeExport typeExport) {

        return exportRandom(method, typeExport, ParamsRandom.create());
    }

    /**
     * Downloads a previously generated test suite from the ecFeed server and parses them to the provided text data format.
     * This method does not start a generator.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the downloaded test cases
     */
    public Iterable<String> exportStatic(String method, TypeExport typeExport, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return export(method, TypeGenerator.Static, typeExport, updatedProperties);
    }

    /**
     * Downloads a previously generated test suite from the ecFeed server and parses them to the provided text data format.
     * This method does not start a generator.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @param properties    a class which contains additional parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the downloaded test cases
     */
    public Iterable<String> exportStatic(String method, TypeExport typeExport, ParamsStatic properties) {

        return exportStatic(method, typeExport, properties.getParamsMap());
    }

    /**
     * Downloads a previously generated test suite from the ecFeed server and parses them to the provided text data format.
     * This method does not start a generator.
     *
     * @param method        the qualified name of the method
     * @param typeExport    the export data format
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the downloaded test cases
     */
    public Iterable<String> exportStatic(String method, TypeExport typeExport) {

        return exportStatic(method, typeExport, ParamsStatic.create());
    }

    /**
     * Generates test cases and returns them as arrays of objects.
     * The method can be integrated with popular testing frameworks, e.g. JUnit5.
     *
     * @param method        the qualified name of the method
     * @param generator     the generator type
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generate(String method, TypeGenerator generator, Map<String, Object> properties) {
        ConfigDefault.processUserParameters(properties);
        ConfigDefault.validateUserParameters(properties);

        DataSession dataSession = Factory.getDataSession(this.dataSessionConnection, this.model, method, generator);

        if (properties.containsKey(ConfigDefault.Key.parSourceClass)) {
            dataSession.getInitializer().source((Class[]) properties.get(ConfigDefault.Key.parSourceClass));
        }

        if (properties.containsKey(ConfigDefault.Key.parSourcePackage)) {
            dataSession.getInitializer().source((String[]) properties.get(ConfigDefault.Key.parSourcePackage));
        }

        dataSession.setOptionsGenerator(properties);

        var iterator = Factory.getIterableTestQueueStream(dataSession);

        new Thread(() -> {
            try {
                connectionHandler.processChunkStream(iterator, connectionHandler.getChunkStreamForTestData(dataSession));
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                iterator.terminate();
            }
        }).start();

        validate(iterator);

        return iterator;
    }

    /**
     * Generates test cases using the n-wise algorithm and returns them as arrays of objects.
     *
     * @param method        the qualified name of the method
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateNWise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return generate(method, TypeGenerator.NWise, updatedProperties);
    }

    /**
     * Generates test cases using the n-wise algorithm and returns them as arrays of objects.
     *
     * @param method        the qualified name of the method
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateNWise(String method, ParamsNWise properties) {

        return generateNWise(method, properties.getParamsMap());
    }

    /**
     * Generates test cases using the n-wise algorithm and returns them as arrays of objects.
     *
     * @param method        the qualified name of the method
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateNWise(String method) {

        return generateNWise(method, ParamsNWise.create());
    }

    /**
     * Generates test cases using the pairwise algorithm and returns them as arrays of objects.
     * Note, that pairwise is specific type of the n-wise algorithm where n=2.
     *
     * @param method        the qualified name of the method
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generatePairwise(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parN, ConfigDefault.Value.parN + "");
        addProperty(updatedProperties, ConfigDefault.Key.parCoverage, ConfigDefault.Value.parCoverage + "");

        return generate(method, TypeGenerator.NWise, updatedProperties);
    }

    /**
     * Generates test cases using the pairwise algorithm and returns them as arrays of objects.
     * Note, that pairwise is specific type of the n-wise algorithm where n=2.
     *
     * @param method        the qualified name of the method
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generatePairwise(String method, ParamsPairwise properties) {

        return generatePairwise(method, properties.getParamsMap());
    }

    /**
     * Generates test cases using the pairwise algorithm and returns them as arrays of objects.
     * Note, that pairwise is specific type of the n-wise algorithm where n=2.
     *
     * @param method        the qualified name of the method
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generatePairwise(String method) {

        return generatePairwise(method, ParamsPairwise.create());
    }

    /**
     * Generates test cases using the cartesian product algorithm and returns them as arrays of objects.
     * Note, this technique can generate a tremendous number of test cases, and therefore, is not recommended.
     *
     * @param method        the qualified name of the method
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateCartesian(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, TypeGenerator.Cartesian, updatedProperties);
    }

    /**
     * Generates test cases using the cartesian product algorithm and returns them as arrays of objects.
     * Note, this technique can generate a tremendous number of test cases, and therefore, is not recommended.
     *
     * @param method        the qualified name of the method
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateCartesian(String method, ParamsCartesian properties) {

        return generateCartesian(method, properties.getParamsMap());
    }

    /**
     * Generates test cases using the cartesian product algorithm and returns them as arrays of objects.
     * Note, this technique can generate a tremendous number of test cases, and therefore, is not recommended.
     *
     * @param method        the qualified name of the method
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateCartesian(String method) {

        return generateCartesian(method, ParamsCartesian.create());
    }

    /**
     * Generates test cases using the random algorithm and returns them as arrays of objects.
     *
     * @param method        the qualified name of the method
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateRandom(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        addProperty(updatedProperties, ConfigDefault.Key.parLength, ConfigDefault.Value.parLength + "");
        addProperty(updatedProperties, ConfigDefault.Key.parAdaptive, ConfigDefault.Value.parAdaptive + "");
        addProperty(updatedProperties, ConfigDefault.Key.parDuplicates, ConfigDefault.Value.parDuplicates + "");

        return generate(method, TypeGenerator.Random, updatedProperties);
    }

    /**
     * Generates test cases using the random algorithm and returns them as arrays of objects.
     *
     * @param method        the qualified name of the method
     * @param properties    a class which contains generation parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateRandom(String method, ParamsRandom properties) {

        return generateRandom(method, properties.getParamsMap());
    }

    /**
     * Generates test cases using the random algorithm and returns them as arrays of objects.
     *
     * @param method        the qualified name of the method
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the generated test cases
     */
    public Iterable<Object[]> generateRandom(String method) {

        return generateRandom(method, ParamsRandom.create());
    }

    /**
     * Downloads a previously generated test suite from the ecFeed server and returns them as arrays of objects.
     * This method does not start a generator.
     *
     * @param method        the qualified name of the method
     * @param properties    optional configuration parameters
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the downloaded test cases
     */
    public Iterable<Object[]> generateStatic(String method, Map<String, Object> properties) {
        Map<String, Object> updatedProperties = new HashMap<>(properties);

        return generate(method, TypeGenerator.Static, updatedProperties);
    }

    /**
     * Downloads a previously generated test suite from the ecFeed server and returns them as arrays of objects.
     * This method does not start a generator.
     *
     * @param method        the qualified name of the method
     * @param properties    a class which contains additional parameters (preferred)
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the downloaded test cases
     */
    public Iterable<Object[]> generateStatic(String method, ParamsStatic properties) {

        return generateStatic(method, properties.getParamsMap());
    }

    /**
     * Downloads a previously generated test suite from the ecFeed server and returns them as arrays of objects.
     * This method does not start a generator.
     *
     * @param method        the qualified name of the method
     * @return              an instance of the class implementing the 'iterable' interface and keeping track of the downloaded test cases
     */
    public Iterable<Object[]> generateStatic(String method) {

        return generateStatic(method, ParamsStatic.create());
    }

    /**
     * Checks whether the connectionHandler with the ecFeed service can be established.
     * In case of an error, and exception is thrown.
     */
    public void validateConnection() {

        connectionHandler.validateConnection(this.dataSessionConnection);
    }

    /**
     * Gets the list of names of method arguments.
     *
     * @param method    the qualified name of the method
     * @return          the list of method argument names
     */
    public List<String> getArgumentNames(String method) {

        return connectionHandler.sendMockRequest(this.dataSessionConnection, this.model, method).getArgumentNames();
    }

    /**
     * Gets the list of types of the method arguments.
     *
     * @param method    the qualified name of the method
     * @return          the list of method argument types.
     */
    public List<String> getArgumentTypes(String method) {

        return connectionHandler.sendMockRequest(this.dataSessionConnection, this.model, method).getArgumentTypes();
    }

    private void addProperty(Map<String, Object> map, String key, String value) {

        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

    private void setup(String model, Map<String, String> config) {

        this.model = model;
        this.dataSessionConnection = Factory.getDataSessionConnection(
                setupExtractGeneratorAddress(config),
                setupExtractKeyStorePath(config),
                setupExtractKeyStorePassword(config)
        );

    }

    private void validate(IterableTestQueue<?> iterator) {
        int timeout = 0;

        do {
            if (iterator.hasNext()) {
                return;
            }

            timeout += ITERATOR_TIMEOUT_STEP;

            try {
                Thread.sleep(ITERATOR_TIMEOUT_STEP);
            } catch (InterruptedException ignored) { }

        } while (timeout < ITERATOR_TIMEOUT);

        validateError();
    }

    private void validateError() {

        if (exceptions.isEmpty()) {
            throw new IllegalArgumentException("The generator stream does not contain any data. Please check if connection parameters are correct.");
        } else if (exceptions.size() == 1) {
            throw new RuntimeException(exceptions.get(0));
        } else {
            validateErrorMerge();
        }
    }

    private void validateErrorMerge() {
        var message = new ArrayList<String>();

        for (int i = 0 ; i < exceptions.size() ; i++) {
            message.add(i + " - " + exceptions.get(i));
        }

        throw new RuntimeException("Multiple errors occurred!\n" + String.join("\n", message));
    }

    private String setupExtractGeneratorAddress(Map<String, String> config) {
        String value = config.get(ConfigDefault.Key.setupGeneratorAddress);

        return value != null ? value : ConfigDefault.Value.generatorAddress;
    }

    private String setupExtractKeyStorePassword(Map<String, String> config) {
        String value = config.get(ConfigDefault.Key.setupKeyStorePassword);

        return value != null ? value : ConfigDefault.Value.keyStorePassword;
    }

    private Path setupExtractKeyStorePath(Map<String, String> config) {
        String value = config.get(ConfigDefault.Key.setupKeyStorePath);

        if (value != null) {
            return getKeyStore(value);
        } else {
            return getKeyStoreDefault();
        }
    }

    private Path getKeyStore(String address) {

        return getKeyStorePath(address);
    }

    private Path getKeyStoreDefault() {

        for (String address : ConfigDefault.Value.keyStorePath) {
            try {
                return getKeyStore(address);
            } catch (IllegalArgumentException ignored) {
            }
        }

        throw new IllegalArgumentException("The keystore could not be loaded");
    }

    private Path getKeyStorePath(String keyStoreAddress) {
        Path keyStorePath = Paths.get(keyStoreAddress);

        if (!Files.exists(keyStorePath)) {
            throw new IllegalArgumentException("The keystore does not exist: " + keyStorePath.toAbsolutePath() + ".");
        }

        if (!Files.isReadable(keyStorePath)) {
            throw new IllegalArgumentException("The keystore is not readable: " + keyStorePath.toAbsolutePath() + ".");
        }

        if (!Files.isRegularFile(keyStorePath)) {
            throw new IllegalArgumentException("The keystore file type is erroneous: " + keyStorePath.toAbsolutePath() + ".");
        }

        return keyStorePath;
    }

}
