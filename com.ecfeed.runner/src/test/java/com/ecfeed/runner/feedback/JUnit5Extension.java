package com.ecfeed.runner.feedback;

import com.ecfeed.TestHandle;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.runner.ConfigDefault;
import com.ecfeed.runner.feedback.controller.TestHandleBox;
import com.ecfeed.runner.feedback.controller.TestHandleExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(TestHandleExtension.class)
public class JUnit5Extension {

    private static ConfigDefault.Stage stage = ConfigDefault.Stage.PROD;

    static Iterable<Object[]> method() {
        return ConfigDefault.getTestProviderRemote(stage).generateNWise(ConfigDefault.F_10x10, ParamsNWise.create().feedback());
    }

    @ParameterizedTest
    @MethodSource("method")
    void defaultNWiseA(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle, TestHandleBox testHandleBox) {
        testHandleBox.registerTestHandle(testHandle);

        Assertions.assertNotEquals("a0", a, "Failed - a");
        Assertions.assertNotEquals("b1", b, "Failed - b");
        Assertions.assertNotEquals("h6", h, "Failed - h");
    }

    @ParameterizedTest
    @MethodSource("method")
    void defaultNWiseB(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, TestHandle testHandle, TestHandleBox testHandleBox) {
        testHandleBox.registerTestHandle(testHandle);

        testHandle.addProperty("key1", "value1");

        Assertions.assertNotEquals("a0", a, "Failed - a");
        Assertions.assertNotEquals("b1", b, "Failed - b");
        Assertions.assertNotEquals("h6", h, "Failed - h");
    }

}
