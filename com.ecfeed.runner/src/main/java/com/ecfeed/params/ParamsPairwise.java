package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

/**
 * This class contains parameters used with the pairwise generator.
 */
public final class ParamsPairwise extends ParamsAbstract<ParamsPairwise> {
    private int n = ConfigDefault.Value.parN;
    private int coverage = ConfigDefault.Value.parCoverage;

    private ParamsPairwise() { }

    /**
     * Creates a parameter group object.
     *
     * @return  pairwise parameter object
     */
    public static ParamsPairwise create() {

        return new ParamsPairwise();
    }

    /**
     * Gets coverage.
     *
     * @return  the coverage value
     */
    public int getCoverage() {

        return coverage;
    }

    /**
     * Sets coverage.
     *
     * @param coverage  the coverage value
     * @return          the current object (can be used for chaining)
     */
    public ParamsPairwise coverage(int coverage) {

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

        paramMap.put(ConfigDefault.Key.parN, n +  "");
        paramMap.put(ConfigDefault.Key.parCoverage, coverage + "");

        return paramMap;
    }

    @Override
    protected ParamsPairwise self() {

        return this;
    }
}
