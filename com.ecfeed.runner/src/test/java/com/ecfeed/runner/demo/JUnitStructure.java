package com.ecfeed.runner.demo;

import com.ecfeed.TestHandle;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.runner.ConfigDefault;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JUnitStructure {

    static Iterable<Object[]> genNWise() {
        return ConfigDefault.getTestProviderRemote(ConfigDefault.Stage.LOCAL_BASIC).generateNWise(ConfigDefault.F_STRUCTURE, ParamsNWise.create()
                        .feedback()
                        .typesDefinitionsSource(Source.class)
                        .label("NWise / Quantity - Single"));
    }

    @ParameterizedTest
    @MethodSource("genNWise")
    void genNWise(Source.Data a, int b, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b );
        testHandle.addFeedback(true);
    }
}
