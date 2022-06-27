package com.ecfeed.params;

import com.ecfeed.config.ConfigDefault;

import java.util.HashMap;
import java.util.Map;

abstract class ParamsAbstract<T extends ParamsAbstract> implements Params {
    private Object constraints = ConfigDefault.Value.parAll;
    private Object choices = ConfigDefault.Value.parAll;
    private boolean feedback = ConfigDefault.Value.parFeedback;
    private String testSessionLabel = "";
    private String template = "";
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

    public String getLabel() {

        return testSessionLabel;
    }

    public T label(String testSessionLabel) {

        this.testSessionLabel = testSessionLabel;
        return self();
    }

    public Map<String, String> getCustom() {

        return custom;
    }

    public T custom(Map<String, String> custom) {

        this.custom = custom;
        return self();
    }

    public String getTemplate() {

        return template;
    }

    public T template(String template) {

        this.template = template;
        return self();
    }

    @Override
    public Map<String, Object> getParamsMap() {
        Map<String, Object> paramMap = new HashMap<>();

        if (this.constraints != null && !this.constraints.toString().equalsIgnoreCase(ConfigDefault.Value.parAll)) {
            paramMap.put(ConfigDefault.Key.parConstraints, this.constraints);
        }

        if (this.choices != null && !this.choices.toString().equalsIgnoreCase(ConfigDefault.Value.parAll)) {
            paramMap.put(ConfigDefault.Key.parChoices, this.choices);
        }

        if (this.feedback) {
            paramMap.put(ConfigDefault.Key.parFeedback, "true");
        }

        if (this.testSessionLabel != null && !this.testSessionLabel.isBlank()) {
            paramMap.put(ConfigDefault.Key.parTestSessionLabel, this.testSessionLabel);
        }

        if (this.custom != null && this.custom.size() > 0) {
            paramMap.put(ConfigDefault.Key.parCustom, this.custom);
        }

        if (this.template != null && !this.template.isBlank()) {
            paramMap.put(ConfigDefault.Key.parDataTemplate, this.template);
        }

        return paramMap;
    }

    protected abstract T self();
}
