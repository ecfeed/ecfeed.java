package com.ecfeed.params;

import com.ecfeed.Config;

import java.util.HashMap;
import java.util.Map;

abstract class ParamsAbstract<T extends ParamsAbstract> {
    private Object constraints = Config.Value.parAll;
    private Object choices = Config.Value.parAll;
    private boolean feedback = Config.Value.parFeedback;
    private String testSessionLabel = "";
    private Map<String, String> custom = new HashMap<>();

    public Object getConstraints() {

        return constraints;
    }

    public T constraints(String[] constraints) {

        this.constraints = constraints;
        return self();
    }

    public T constraints(String constraints) {

        this.constraints = constraints;
        return self();
    }

    public Object getChoices() {

        return choices;
    }

    public T choices(Map<String, String[]> choices) {

        this.choices = choices;
        return self();
    }

    public T choices(String choices) {

        this.choices = choices;
        return self();
    }

    public boolean getFeedback() {

        return this.feedback;
    }

    public T feedback() {

        this.feedback = true;
        return self();
    }

    public String getTestSessionLabel() {

        return testSessionLabel;
    }

    public T setTestSessionLabel(String testSessionLabel) {

        this.testSessionLabel = testSessionLabel;
        return self();
    }

    public Map<String, String> getCustom() {

        return custom;
    }

    public T setCustom(Map<String, String> custom) {

        this.custom = custom;
        return self();
    }

    protected Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = new HashMap<>();

        if (this.constraints != null && !this.constraints.toString().equalsIgnoreCase(Config.Value.parAll)) {
            paramMap.put(Config.Key.parConstraints, this.constraints);
        }

        if (this.choices != null && !this.choices.toString().equalsIgnoreCase(Config.Value.parAll)) {
            paramMap.put(Config.Key.parChoices, this.choices);
        }

        if (this.feedback) {
            paramMap.put(Config.Key.parFeedback, "true");
        }

        if (this.testSessionLabel != null && !this.testSessionLabel.equalsIgnoreCase("")) {
            paramMap.put(Config.Key.parTestSessionLabel, this.testSessionLabel);
        }

        if (this.custom != null && this.custom.size() > 0) {
            paramMap.put(Config.Key.parCustom, this.custom);
        }

        return paramMap;
    }

    protected abstract T self();
}
