package com.ecfeed.runner.export;

import com.ecfeed.params.ParamsNWise;
import com.ecfeed.runner.ConfigDefault;
import com.ecfeed.runner.demo.Source;
import com.ecfeed.type.TypeExport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;

public class TemplateCSV {

    private static ConfigDefault.Stage stage = ConfigDefault.Stage.LOCAL_TEAM;

    static Iterable<Object[]> genNWise() {
        return ConfigDefault.getTestProviderRemote(stage).generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create()
                .typesDefinitionsSource(Source.class)
                .label("NWise"));
    }

    static Iterable<String> expNWiseJSON() {
        return ConfigDefault.getTestProviderRemote(stage).exportNWise(ConfigDefault.F_STRUCTURE, TypeExport.JSON);
    }

    static Iterable<String> expNWiseRFC4627() {

        return ConfigDefault.getTestProviderRemote(stage).exportNWise(ConfigDefault.F_STRUCTURE, TypeExport.RFC_4627.setIndent(0));
    }

    static Iterable<String> expNWiseCSV() {
        return ConfigDefault.getTestProviderRemote(stage).exportNWise(ConfigDefault.F_STRUCTURE, TypeExport.CSV);
    }

    static Iterable<String> expNWiseRFC4180() {

        return ConfigDefault.getTestProviderRemote(stage).exportNWise(ConfigDefault.F_STRUCTURE, TypeExport.RFC_4180.setDelimiter(";"));
    }

    @ParameterizedTest
    @MethodSource("genNWise")
    void genNWise(Source.Person a, int b) {
        System.out.println("a = " + a + ", b = " + b );
    }

    @ParameterizedTest
    @MethodSource("expNWiseJSON")
    void expNWiseJSON(String txt) {
        System.out.println(txt);
    }

    @ParameterizedTest
    @MethodSource("expNWiseRFC4627")
    void expNWiseRFC4627(String txt) {
        System.out.println(txt);
    }

    @ParameterizedTest
    @MethodSource("expNWiseCSV")
    void expNWiseCSV(String txt) {
        System.out.println(txt);
    }

    @ParameterizedTest
    @MethodSource("expNWiseRFC4180")
    void expNWiseRFC4180(String txt) {
        System.out.println(txt);
    }
}
