package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

public final class ParamsStatic extends ParamsAbstract<ParamsStatic> {
    private Object testSuites = ConfigDefault.Value.parAll;

    private ParamsStatic() { }

    public static ParamsStatic create() {

        return new ParamsStatic();
    }

    public Object getTestSuites() {

        return testSuites;
    }

    public ParamsStatic testSuites(String[] testSuites) {

        this.testSuites = testSuites;
        return self();
    }

    public ParamsStatic testSuites(String testSuites) {

        this.testSuites = testSuites;
        return self();
    }

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
