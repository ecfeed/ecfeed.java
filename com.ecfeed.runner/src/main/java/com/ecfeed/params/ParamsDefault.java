package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

class ParamsDefault extends ParamsAbstract<ParamsDefault> {
    private int n = ConfigDefault.Value.parN;
    private int coverage = ConfigDefault.Value.parCoverage;
    private int length = ConfigDefault.Value.parLength;
    private boolean adaptive = ConfigDefault.Value.parAdaptive;
    private boolean duplicates = ConfigDefault.Value.parDuplicates;
    private Object testSuites = ConfigDefault.Value.parAll;

    private ParamsDefault() { }

    public static ParamsDefault create() {

        return new ParamsDefault();
    }

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

        paramMap.put(ConfigDefault.Key.parN, n + "");
        paramMap.put(ConfigDefault.Key.parCoverage, coverage + "");
        paramMap.put(ConfigDefault.Key.parLength, length + "");
        paramMap.put(ConfigDefault.Key.parAdaptive, adaptive + "");
        paramMap.put(ConfigDefault.Key.parDuplicates, duplicates + "");
        paramMap.put(ConfigDefault.Key.parTestSuites, testSuites);

        return paramMap;
    }

    @Override
    protected ParamsDefault self() {

        return this;
    }
}
