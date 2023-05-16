package com.ecfeed.session.dto;

import com.ecfeed.config.ConfigDefault;
import com.ecfeed.structure.StructureInitializer;
import com.ecfeed.structure.StructureInitializerDefault;
import com.ecfeed.type.TypeExport;
import com.ecfeed.type.TypeGenerator;
import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.*;

public class DataSession {

    private final JSONObject testResults = new JSONObject();

    private final DataSessionConnection connection;
    private final TypeGenerator generator;
    private final String method;
    private final String model;

    private final String runner;

    private final StructureInitializer initializer = new StructureInitializerDefault();

    private List<String> argumentTypes = new ArrayList<>();
    private List<String> argumentNames = new ArrayList<>();

    private Map<String, Object> optionsGenerator = new HashMap<>();
    private Map<String, String> optionsCustom = new HashMap<>();
    private String exportTemplate = "";
    private String methodNameSignature = "";
    private String methodNameQualified = "";
    private String testSessionId = "";
    private String testSessionLabel = "";
    private Object constraints = ConfigDefault.Value.parAll;
    private Object testSuites = ConfigDefault.Value.parAll;
    private Object choices = ConfigDefault.Value.parAll;

    private int timestamp = -1;

    private boolean feedbackEnabled = false;
    private boolean feedbackCompleted = false;

    private int testCasesTotal = 0;
    private int testCasesParsed = 0;

    private DataSession(DataSessionConnection connection, String model, String method, TypeGenerator generator) {
        this.connection = connection;
        this.method = method;
        this.generator = generator;

        if (model.contains(":")) {
            var elements = model.split(":");

            if (elements.length == 2) {
                this.model = elements[0];
                this.runner = elements[1];
            } else {
                this.model = elements[0];
                this.runner = "";
            }
        } else {
            this.model = model;
            this.runner = "";
        }
    }

    public static DataSession create(DataSessionConnection connection, String model, String method, TypeGenerator generatorType) {

        return new DataSession(connection, model, method, generatorType);
    }

    public JSONObject getTestResults() {

        return testResults;
    }

    public DataSessionConnection getConnection() {

        return connection;
    }

    public TypeGenerator getGenerator() {

        return generator;
    }

    public String getMethodName() {

        return method;
    }

    public String getModel() {

        return model;
    }

    public String getRunner() {

        return runner;
    }

    public StructureInitializer getInitializer() {

        return initializer;
    }

    public List<String> getArgumentTypes() {

        return this.argumentTypes;
    }

    public List<String> getArgumentNames() {

        return this.argumentNames;
    }

    public Map<String, Object> getOptionsGenerator() {

        return optionsGenerator;
    }

    public Map<String, String> getOptionsCustom() {

        return optionsCustom;
    }

    public Optional<String> getExportTemplate() {

        if (exportTemplate == null || exportTemplate.isBlank() || exportTemplate.equals(TypeExport.Raw.toString())) {
            return Optional.empty();
        }

        return Optional.of(exportTemplate);
    }

    public String getMethodNameSignature() {

        return methodNameSignature;
    }

    public String getMethodNameQualified() {

        return methodNameQualified;
    }

    public String getTestSessionId() {

        return this.testSessionId;
    }

    public String getTestSessionLabel() {

        return this.testSessionLabel;
    }

    public Object getConstraints() {

        return constraints;
    }

    public Object getTestSuites() {

        return testSuites;
    }

    public Object getChoices() {

        return choices;
    }

    public int getTimestamp() {

        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {

        this.timestamp = timestamp;
    }

    public boolean isFeedbackEnabled() {

        return feedbackEnabled;
    }

    public boolean isFeedbackCompleted() {

        return feedbackCompleted;
    }

    public int getTestCasesTotal() {

        return testCasesTotal;
    }

    public int getTestCasesParsed() {

        return testCasesParsed;
    }

//-----------------------------------------------------------------------------

    public void setOptionsGenerator(Map<String, Object> properties) {
        this.optionsGenerator = new HashMap<>();

        properties.forEach((key, value) -> {
            if (key.equalsIgnoreCase(ConfigDefault.Key.parConstraints)) {
                constraints = value;
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parTestSuites)) {
                testSuites = value;
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parChoices)) {
                choices = value;
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parTestSessionLabel)) {
                testSessionLabel = value.toString();
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parCustom)) {
                optionsCustom = (Map<String, String>) value;
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parFeedback)) {
                if (value.equals(true) || value.toString().equalsIgnoreCase("true")) {
                    feedbackEnabled = true;
                }
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parSourceClass)) {
            } else if (key.equalsIgnoreCase(ConfigDefault.Key.parSourcePackage)) {
            } else {
                this.optionsGenerator.put(key, value);
            }
        });

    }

    public void setExportTemplate(String exportTemplate) {

        this.exportTemplate = exportTemplate;
    }

    public void setMethodNameSignature(String methodNameSignature) {

        this.methodNameSignature = methodNameSignature;
    }

    public void setMethodNameQualified(String methodNameQualified) {

        this.methodNameQualified = methodNameQualified;
    }

    public void setTestSessionId(String testSessionId) {

        this.testSessionId = testSessionId;
    }

    public void setFeedbackCompleted() {

        this.feedbackCompleted = true;
    }

    public void incTestCasesParsed() {

        this.testCasesParsed++;
    }

    public void incTestCasesTotal() {

        this.testCasesTotal++;
    }

    public void addTestCase(String id, JSONObject feedback) {

        this.testResults.put(id, feedback);
    }

    public void addArgumentType(String argumentType) {

        this.argumentTypes.add(argumentType);
    }

    public void addArgumentName(String argumentName) {

        this.argumentNames.add(argumentName);
    }

//-----------------------------------------------------------------------------

    public HttpClient getHttpClient() {

        return connection.getHttpClient();
    }

    public String getHttpAddress() {

        return connection.getHttpAddress();
    }

    public Path getKeyStorePath() {

        return connection.getKeyStorePath();
    }

//-----------------------------------------------------------------------------
}
