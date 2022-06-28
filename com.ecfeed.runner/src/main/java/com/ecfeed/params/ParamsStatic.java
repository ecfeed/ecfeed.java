package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

/**
 * This class contains parameters used with the static downloader.
 */
public final class ParamsStatic extends ParamsAbstract<ParamsStatic> {
    private Object testSuites = ConfigDefault.Value.parAll;

    private ParamsStatic() { }

    /**
     * Creates a parameter group object.
     *
     * @return  static parameter object
     */
    public static ParamsStatic create() {

        return new ParamsStatic();
    }

    /**
     * Gets test suites.
     *
     * @return  a test suite mode ar an array of test suites
     */
    public Object getTestSuites() {

        return testSuites;
    }

    /**
     * Sets test suites.
     *
     * @param testSuites    an array of test suite names
     * @return              the current object (can be used for chaining)
     */
    public ParamsStatic testSuites(String[] testSuites) {

        this.testSuites = testSuites;
        return self();
    }

    /**
     * Sets test suites.
     *
     * @param testSuites    the test suite mode, e.g. ALL
     * @return              the current object (can be used for chaining)
     */
    public ParamsStatic testSuites(String testSuites) {

        this.testSuites = testSuites;
        return self();
    }

    /**
     * Gets all generation parameters.
     *
     * @return  generation parameters
     */
    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = super.getParamsMap();

        paramMap.put(ConfigDefault.Key.parTestSuites, testSuites);

        return paramMap;
    }

    @Override
    protected ParamsStatic self() {

        return this;
    }
}
