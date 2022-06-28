package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

/**
 * This class contains parameters used with the n-wise generator.
 */
public final class ParamsNWise extends ParamsAbstract<ParamsNWise> {
    private int n = ConfigDefault.Value.parN;
    private int coverage = ConfigDefault.Value.parCoverage;

    private ParamsNWise() { }

    /**
     * Creates a parameter group object.
     *
     * @return  n-wise parameter object
     */
    public static ParamsNWise create() {

        return new ParamsNWise();
    }

    /**
     * Gets the N value.
     *
     * @return the N value
     */
    public int getN() {

        return this.n;
    }

    /**
     * Sets the N value.
     *
     * @param n the N value
     * @return  the current object (can be used for chaining)
     */
    public ParamsNWise n(int n) {

        this.n = n;
        return self();
    }

    /**
     * Gets coverage.
     *
     * @return  the coverage value
     */
    public int getCoverage() {

        return this.coverage;
    }

    /**
     * Sets coverage.
     *
     * @param coverage  the coverage value
     * @return          the current object (can be used for chaining)
     */
    public ParamsNWise coverage(int coverage) {

        this.coverage = coverage;
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

        paramMap.put(ConfigDefault.Key.parN, n + "");
        paramMap.put(ConfigDefault.Key.parCoverage, coverage + "");

        return paramMap;
    }

    @Override
    protected ParamsNWise self() {

        return this;
    }
}