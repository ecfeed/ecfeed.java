package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

/**
 * This class contains parameters used with the random generator.
 */
public final class ParamsRandom extends ParamsAbstract<ParamsRandom> {
    private int length = ConfigDefault.Value.parLength;
    private boolean adaptive = ConfigDefault.Value.parAdaptive;
    private boolean duplicates = ConfigDefault.Value.parDuplicates;

    private ParamsRandom() { }

    /**
     * Creates a parameter group object.
     *
     * @return  random parameter object
     */
    public static ParamsRandom create() {

        return new ParamsRandom();
    }

    /**
     * Gets length.
     *
     * @return  the length value
     */
    public int getLength() {

        return this.length;
    }

    /**
     * Sets length.
     *
     * @param length    the length value
     * @return          the current object (can be used for chaining)
     */
    public ParamsRandom length(int length) {

        this.length = length;
        return self();
    }

    /**
     * Gets adaptive.
     *
     * @return  is generation adaptive.
     */
    public boolean getAdaptive() {

        return this.adaptive;
    }

    /**
     * Sets adaptive.
     *
     * @param adaptive  make generation adaptive.
     * @return          the current object (can be used for chaining)
     */
    public ParamsRandom adaptive(boolean adaptive) {

        this.adaptive = adaptive;
        return self();
    }

    /**
     * Gets duplicates.
     *
     * @return  the duplicates value.
     */
    public boolean getDuplicates() {

        return this.duplicates;
    }

    /**
     * Set duplicates.
     *
     * @param duplicates    enable duplicated test cases.
     * @return              the current object (can be used for chaining)
     */
    public ParamsRandom duplicates(boolean duplicates) {

        this.duplicates = duplicates;
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

        paramMap.put(ConfigDefault.Key.parLength, length + "");
        paramMap.put(ConfigDefault.Key.parAdaptive, adaptive + "");
        paramMap.put(ConfigDefault.Key.parDuplicates, duplicates + "");

        return paramMap;
    }

    @Override
    protected ParamsRandom self() {

        return this;
    }
}
