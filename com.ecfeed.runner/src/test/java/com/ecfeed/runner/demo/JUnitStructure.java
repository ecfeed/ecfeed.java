package com.ecfeed.runner.demo;

import com.ecfeed.TestHandle;
import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.params.ParamsRandom;
import com.ecfeed.runner.ConfigDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JUnitStructure {

    static Iterable<Object[]> genNWise() {
        return ConfigDefault.getTestProvider(ConfigDefault.DEVELOP).generateNWise(ConfigDefault.F_TEST, ParamsNWise.create()
                .feedback()
                .label("NWise / Quantity - Single"));
    }

    @ParameterizedTest
    @MethodSource("genNWise")
    void genNWise(int a, int b, int c, TestHandle testHandle) {
        System.out.println("a = " + a + ", b = " + b + ", c = " + c);
    }

    @Test
    @DisplayName("Get method names")
    void getMethodSignature() {
        TestProvider testProvider = ConfigDefault.getTestProvider(ConfigDefault.DEVELOP);

        System.out.println(testProvider.getArgumentNames(ConfigDefault.F_STRUCTURE));
    }
}
