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
    private Class[] typeDefinitionSourceClass = new Class[0];
    private String[] typeDefinitionSourcePackage = new String[0];
    private Map<String, String> custom = new HashMap<>();

    /**
     * Gets constrains.
     *
     * @return  a constraint mode ar an array of constraints
     */
    public Object getConstraints() {

        return constraints;
    }

    /**
     * Sets constraints.
     *
     * @param constraints   an array of constraint names
     * @return              the current object (can be used for chaining)
     */
    public T constraints(String[] constraints) {

        this.constraints = constraints;
        return self();
    }

    /**
     * Sets constraints.
     *
     * @param constraints   the constraint mode, e.g. ALL, NONE
     * @return              the current object (can be used for chaining)
     */
    public T constraints(String constraints) {

        this.constraints = constraints;
        return self();
    }

    /**
     * Gets choices.
     *
     * @return  a choice mode or a map of choices.
     */
    public Object getChoices() {

        return choices;
    }

    /**
     * Sets choices.
     *
     * @param choices   a map of choices.
     * @return          the current object (can be used for chaining)
     */
    public T choices(Map<String, String[]> choices) {

        this.choices = choices;
        return self();
    }

    /**
     * Sets choices.
     *
     * @param choices   the choice mode, e.g. ALL
     * @return          the current object (can be used for chaining)
     */
    public T choices(String choices) {

        this.choices = choices;
        return self();
    }

    /**
     * Checks whether feedback is active.
     *
     * @return  is feedback active.
     */
    public boolean getFeedback() {

        return this.feedback;
    }

    /**
     * Enables feedback.
     *
     * @return  the current object (can be used for chaining)
     */
    public T feedback() {

        this.feedback = true;
        return self();
    }

    /**
     * Gets test session label.
     *
     * @return  test session label
     */
    public String getLabel() {

        return testSessionLabel;
    }

    /**
     * Sets test session label.
     *
     * @param testSessionLabel  test session label
     * @return                  the current object (can be used for chaining)
     */
    public T label(String testSessionLabel) {

        this.testSessionLabel = testSessionLabel;
        return self();
    }

    /**
     * Gets map of custom parameters.
     *
     * @return  the current object (can be used for chaining)
     */
    public Map<String, String> getCustom() {

        return custom;
    }

    /**
     * Sets map of custom parameters.
     *
     * @param custom    map of custom parameters
     * @return          the current object (can be used for chaining)
     */
    public T custom(Map<String, String> custom) {

        this.custom = custom;
        return self();
    }

    /**
     * Gets export template.
     *
     * @return  export template
     */
    public String getTemplate() {

        return template;
    }

    /**
     * Sets export template
     *
     * @param template  export template
     * @return          the current object (can be used for chaining)
     */
    public T template(String template) {

        this.template = template;
        return self();
    }

    /**
     * Gets source classes.
     *
     * @return  source classes
     */
    public Class[] getTypeDefinitionSourceClass() {

        return typeDefinitionSourceClass;
    }

    /**
     * Sets source classes.
     *
     * @param source    source classes
     * @return          the current object (can be used for chaining)
     */
    public T typesDefinitionsSource(Class... source) {

        this.typeDefinitionSourceClass = source;
        return self();
    }

    /**
     * Gets source packages.
     *
     * @return  source packages
     */
    public String[] getTypeDefinitionSourcePackage() {

        return typeDefinitionSourcePackage;
    }

    /**
     * Sets source packages.
     *
     * @param source    source packages
     * @return          the current object (can be used for chaining)
     */
    public T typesDefinitionsSource(String... source) {

        this.typeDefinitionSourcePackage = source;
        return self();
    }

    /**
     * Gets all generation parameters
     *
     * @return  generation parameters
     */
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

        if (this.typeDefinitionSourceClass != null) {
            paramMap.put(ConfigDefault.Key.parSourceClass, this.typeDefinitionSourceClass);
        }

        if (this.typeDefinitionSourcePackage != null) {
            paramMap.put(ConfigDefault.Key.parSourcePackage, this.typeDefinitionSourcePackage);
        }

        return paramMap;
    }

    protected abstract T self();
}
