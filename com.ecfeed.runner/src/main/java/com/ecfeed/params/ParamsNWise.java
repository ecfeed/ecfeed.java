package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

public final class ParamsNWise extends ParamsAbstract<ParamsNWise> {
    private int n = ConfigDefault.Value.parN;
    private int coverage = ConfigDefault.Value.parCoverage;

    private ParamsNWise() { }

    public static ParamsNWise create() {

        return new ParamsNWise();
    }

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

        paramMap.put(ConfigDefault.Key.parN, n + "");
        paramMap.put(ConfigDefault.Key.parCoverage, coverage + "");

        return paramMap;
    }

    @Override
    protected ParamsNWise self() {

        return this;
    }
}