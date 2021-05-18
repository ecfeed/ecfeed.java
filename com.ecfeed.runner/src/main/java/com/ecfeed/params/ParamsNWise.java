package com.ecfeed.params;

import com.ecfeed.Config;

import java.util.Map;

public final class ParamsNWise extends ParamsAbstract<ParamsNWise> {
    private int n = Config.Value.parN;
    private int coverage = Config.Value.parCoverage;

    public int getN() {

        return this.n;
    }

    public ParamsNWise n(int n) {

        this.n = n;
        return self();
    }

    public int getCoverage() {

        return this.coverage;
    }

    public ParamsNWise coverage(int coverage) {

        this.coverage = coverage;
        return self();
    }

    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = super.getParamsMap();

        paramMap.put(Config.Key.parN, n + "");
        paramMap.put(Config.Key.parCoverage, coverage + "");

        return paramMap;
    }

    @Override
    protected ParamsNWise self() {

        return this;
    }
}