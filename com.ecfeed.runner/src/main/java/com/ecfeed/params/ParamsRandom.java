package com.ecfeed.params;

import com.ecfeed.Config;

import java.util.Map;

public final class ParamsRandom extends ParamsAbstract<ParamsRandom> {
    private int length = Config.Value.parLength;
    private boolean adaptive = Config.Value.parAdaptive;
    private boolean duplicates = Config.Value.parDuplicates;

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

        paramMap.put(Config.Key.parLength, length + "");
        paramMap.put(Config.Key.parAdaptive, adaptive + "");
        paramMap.put(Config.Key.parDuplicates, duplicates + "");

        return paramMap;
    }

    @Override
    protected ParamsRandom self() {

        return this;
    }
}
