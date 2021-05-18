package com.ecfeed.params;

import com.ecfeed.Config;

import java.util.Map;

public final class ParamsPairwise extends ParamsAbstract<ParamsPairwise> {
    private int n = Config.Value.parN;
    private int coverage = Config.Value.parCoverage;

    public int getCoverage() {

        return coverage;
    }

    public ParamsPairwise coverage(int coverage) {

        this.coverage = coverage;
        return self();
    }

    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = super.getParamsMap();

        paramMap.put(Config.Key.parN, n +  "");
        paramMap.put(Config.Key.parCoverage, coverage + "");

        return paramMap;
    }

    @Override
    protected ParamsPairwise self() {

        return this;
    }
}
