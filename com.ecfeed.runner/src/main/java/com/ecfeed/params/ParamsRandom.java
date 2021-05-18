package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.Map;

public final class ParamsRandom extends ParamsAbstract<ParamsRandom> {
    private int length = ConfigDefault.Value.parLength;
    private boolean adaptive = ConfigDefault.Value.parAdaptive;
    private boolean duplicates = ConfigDefault.Value.parDuplicates;

    private ParamsRandom() { }

    public static ParamsRandom create() {

        return new ParamsRandom();
    }

    public int getLength() {

        return this.length;
    }

    public ParamsRandom length(int length) {

        this.length = length;
        return self();
    }

    public boolean getAdaptive() {

        return this.adaptive;
    }

    public ParamsRandom adaptive(boolean adaptive) {

        this.adaptive = adaptive;
        return self();
    }

    public boolean getDuplicates() {

        return this.duplicates;
    }

    public ParamsRandom duplicates(boolean duplicates) {

        this.duplicates = duplicates;
        return self();
    }

    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = super.getParamsMap();

        paramMap.put(ConfigDefault.Key.parLength, length + "");
        paramMap.put(ConfigDefault.Key.parAdaptive, adaptive + "");
        paramMap.put(ConfigDefault.Key.parDuplicates, duplicates + "");

        return paramMap;
    }

    @Override
    protected ParamsRandom self() {

        return this;
    }
}
