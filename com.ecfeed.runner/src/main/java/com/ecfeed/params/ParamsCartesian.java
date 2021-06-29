package com.ecfeed.params;

import java.util.Map;

public final class ParamsCartesian extends ParamsAbstract<ParamsCartesian> {

    private ParamsCartesian() { }

    public static ParamsCartesian create() {

        return new ParamsCartesian();
    }

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
