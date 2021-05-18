package com.ecfeed.params;

import com.ecfeed.Config;

import java.util.Map;

public final class ParamsStatic extends ParamsAbstract<ParamsStatic> {
    private Object testSuites = Config.Value.parAll;

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

        paramMap.put(Config.Key.parTestSuites, testSuites);

        return paramMap;
    }

    @Override
    protected ParamsStatic self() {

        return this;
    }
}
