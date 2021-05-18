package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

public final class ParamsPairwise extends ParamsAbstract<ParamsPairwise> {
    private int n = ConfigDefault.Value.parN;
    private int coverage = ConfigDefault.Value.parCoverage;

    private ParamsPairwise() { }

    public static ParamsPairwise create() {

        return new ParamsPairwise();
    }

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

        paramMap.put(ConfigDefault.Key.parN, n +  "");
        paramMap.put(ConfigDefault.Key.parCoverage, coverage + "");

        return paramMap;
    }

    @Override
    protected ParamsPairwise self() {

        return this;
    }
}
