package com.ecfeed.params;

import com.ecfeed.Config;

import java.util.Map;

public class ParamsDefault extends ParamsAbstract<ParamsDefault> {
    private int n = Config.Value.parN;
    private int coverage = Config.Value.parCoverage;
    private int length = Config.Value.parLength;
    private boolean adaptive = Config.Value.parAdaptive;
    private boolean duplicates = Config.Value.parDuplicates;
    private Object testSuites = Config.Value.parAll;

    public int getN() {

        return this.n;
    }

    public ParamsDefault n(int n) {

        this.n = n;
        return self();
    }

    public int getCoverage() {

        return this.coverage;
    }

    public ParamsDefault coverage(int coverage) {

        this.coverage = coverage;
        return self();
    }

    public int getLength() {

        return this.length;
    }

    public ParamsDefault length(int length) {

        this.length = length;
        return self();
    }

    public boolean getAdaptive() {

        return this.adaptive;
    }

    public ParamsDefault adaptive(boolean adaptive) {

        this.adaptive = adaptive;
        return self();
    }

    public boolean getDuplicates() {

        return this.duplicates;
    }

    public ParamsDefault duplicates(boolean duplicates) {

        this.duplicates = duplicates;
        return self();
    }

    public Object getTestSuites() {

        return testSuites;
    }

    public ParamsDefault testSuites(String[] testSuites) {

        this.testSuites = testSuites;
        return self();
    }

    public ParamsDefault testSuites(String testSuites) {

        this.testSuites = testSuites;
        return self();
    }

    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = super.getParamsMap();

        paramMap.put(Config.Key.parN, n + "");
        paramMap.put(Config.Key.parCoverage, coverage + "");
        paramMap.put(Config.Key.parLength, length + "");
        paramMap.put(Config.Key.parAdaptive, adaptive + "");
        paramMap.put(Config.Key.parDuplicates, duplicates + "");
        paramMap.put(Config.Key.parTestSuites, testSuites);

        return paramMap;
    }

    @Override
    protected ParamsDefault self() {

        return this;
    }
}
