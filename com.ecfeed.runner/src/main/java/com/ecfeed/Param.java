package com.ecfeed;

import java.util.HashMap;
import java.util.Map;

public class Param{

    private static abstract class ParamsCommon<T extends ParamsCommon> {
        private Object constraints;
        private Object choices;

        public Object getConstraints() {

            return constraints;
        }

        public Object getChoices() {

            return choices;
        }

        public T constraints(String[] constraints) {

            this.constraints = constraints;
            return self();
        }

        public T constraints(String constraints) {

            this.constraints = constraints;
            return self();
        }

        public T choices(Map<String, String[]> choices) {

            this.choices = choices;
            return self();
        }

        public T choices(String choices) {

            this.choices = choices;
            return self();
        }

        protected Map<String, Object> getParamMap() {
            Map<String, Object> paramMap = new HashMap<>();

            if (constraints != null) {
                paramMap.put(Config.Key.parConstraints, constraints);
            }

            if (choices != null) {
                paramMap.put(Config.Key.parChoices, choices);
            }


            return paramMap;
        }

        protected abstract T self();
    }

    public final static class ParamsNWise extends ParamsCommon<ParamsNWise> {
        private String n = Config.Value.parN;
        private String coverage = Config.Value.parCoverage;

        public int getN() {

            return Integer.parseInt(n);
        }

        public int getCoverage() {

            return Integer.parseInt(coverage);
        }

        public ParamsNWise n(int n) {

            this.n = n + "";
            return self();
        }

        public ParamsNWise coverage(int coverage) {

            this.coverage = coverage + "";
            return self();
        }

        @Override
        public Map<String, Object> getParamMap() {
            Map<String, Object> paramMap = super.getParamMap();

            paramMap.put(Config.Key.parN, n +  "");
            paramMap.put(Config.Key.parCoverage, coverage + "");

            return paramMap;
        }

        @Override
        protected ParamsNWise self() {

            return this;
        }
    }

    public final static class ParamsPairwise extends ParamsCommon<ParamsPairwise> {
        private String n = Config.Value.parN;
        private String coverage = Config.Value.parCoverage;

        public int getCoverage() {

            return Integer.parseInt(coverage);
        }

        public ParamsPairwise coverage(int coverage) {

            this.coverage = coverage + "";
            return self();
        }

        @Override
        public Map<String, Object> getParamMap() {
            Map<String, Object> paramMap = super.getParamMap();

            paramMap.put(Config.Key.parN, n +  "");
            paramMap.put(Config.Key.parCoverage, coverage + "");

            return paramMap;
        }

        @Override
        protected ParamsPairwise self() {

            return this;
        }
    }

    public final static class ParamsCartesian extends ParamsCommon<ParamsCartesian> {

        @Override
        public Map<String, Object> getParamMap() {
            Map<String, Object> paramMap = super.getParamMap();

            return paramMap;
        }

        @Override
        protected ParamsCartesian self() {

            return this;
        }
    }

    public final static class ParamsRandom extends ParamsCommon<ParamsRandom> {
        private String length = Config.Value.parLength;
        private String adaptive = Config.Value.parAdaptive;
        private String duplicates = Config.Value.parDuplicates;

        public int getLength() {

            return Integer.parseInt(length);
        }

        public boolean getAdaptive() {

            return Boolean.parseBoolean(adaptive);
        }

        public boolean getDuplicates() {

            return Boolean.parseBoolean(duplicates);
        }

        public ParamsRandom length(int length) {

            this.length = length + "";
            return self();
        }

        public ParamsRandom adaptive(boolean adaptive) {

            this.adaptive = adaptive + "";
            return self();
        }

        public ParamsRandom duplicates(boolean duplicates) {

            this.duplicates = duplicates + "";
            return self();
        }

        @Override
        public Map<String, Object> getParamMap() {
            Map<String, Object> paramMap = super.getParamMap();

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

    public final static class ParamsStatic extends ParamsCommon<ParamsStatic> {
        private Object testSuites;

        public Object getTestSuites() {

            return testSuites;
        }

        public ParamsStatic testSuites(String[] testSuites) {

            this.testSuites = testSuites;
            return self();
        }

        public ParamsStatic testSuites(String testSuites) {

            this.testSuites = testSuites;
            return self();
        }

        @Override
        public Map<String, Object> getParamMap() {
            Map<String, Object> paramMap = super.getParamMap();

            paramMap.put(Config.Key.parTestSuites, testSuites);

            return paramMap;
        }

        @Override
        protected ParamsStatic self() {

            return this;
        }
    }
}
