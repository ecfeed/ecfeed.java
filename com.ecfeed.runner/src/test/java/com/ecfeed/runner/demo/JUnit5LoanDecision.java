package com.ecfeed.runner.demo;

import com.ecfeed.TestProvider;
import com.ecfeed.params.ParamsNWise;
import com.ecfeed.runner.ConfigDefault;
import com.ecfeed.type.TypeExport;
import org.junit.jupiter.api.Test;

public class JUnit5LoanDecision {

    @Test
    void exportTypeCustom() {
        var template = "[Header]\n" +
                "{ \n" +
                "\t\"testCases\" : [\n" +
                "[TestCase]\n" +
                "\t\t{\n" +
                "\t\t\t\"index\": %index, \n" +
                "\t\t\t\"group1\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"$1.name\":\"$1.value\", \n" +
                "\t\t\t\t\"$2.name\":\"$2.value\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"group2\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"group21\":\n" +
                "\t\t\t\t{ \n" +
                "\t\t\t\t\t\"$3.name\":\"$3.value\", \n" +
                "\t\t\t\t\t\"$4.name\":\"$4.value\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"$5.name\":\"$5.value\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"group3\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"group31\":\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"group311\":\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"group3111\":\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"$6.name\":\"$6.value\", \n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t} \n" +
                "\t\t},\n" +
                "[Footer]\n" +
                "\t]\n" +
                "} ";

        TestProvider testProvider = ConfigDefault.getTestProviderRemote(ConfigDefault.PROD);

        for (String chunk : testProvider.exportNWise(ConfigDefault.F_LOAN_2, TypeExport.Custom, ParamsNWise.create().template(template))) {
            System.out.println(chunk);
        }
    }

}
