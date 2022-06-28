package com.ecfeed.params;

import java.util.Map;

/**
 * This class contains parameters used with the cartesian product generator.
 */
public final class ParamsCartesian extends ParamsAbstract<ParamsCartesian> {

    private ParamsCartesian() { }

    /**
     * Creates a parameter group object.
     *
     * @return  cartesian product parameter object
     */
    public static ParamsCartesian create() {

        return new ParamsCartesian();
    }

    /**
     * Gets all generation parameters.
     *
     * @return  generation parameters
     */
    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = super.getParamsMap();

        return paramMap;
    }

    @Override
    protected ParamsCartesian self() {

        return this;
    }
}
