package com.ecfeed.runner.demo;

import com.ecfeed.Factory;
import com.ecfeed.data.DataSession;
import com.ecfeed.data.DataSessionConnection;
import com.ecfeed.runner.ConfigDefault;
import com.ecfeed.type.TypeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

public class JUnit5Manual {

    @Test
    @DisplayName("Manual basic")
    void manualBasic() {
        var dataSession = DataSession.create(
                DataSessionConnection.get(
                        ConfigDefault.GENERATOR_ADDRESS_DEVELOP,
                        Path.of(ConfigDefault.KEYSTORE_DEVELOP),
                        ConfigDefault.KEYSTORE_PASSWORD
                ),
                ConfigDefault.MODEL_DEVELOP, ConfigDefault.F_TEST, TypeGenerator.Random);

       var parser = Factory.getChunkParserStream(dataSession);

        Optional<Object[]> result;

        result = parser.parse("{\"status\":\"BEG_DATA\"}");
        result = parser.parse("{\"info\":\"{'method':'QuickStart.test(int arg1, int arg2, int arg3)','testSessionId':'00dc385f712da97a4ab9ba63e881158171a252c78865cf25dc7f5766675936b4','timestamp':'1678963446'}\"}");
        result = parser.parse("{\"status\":\"BEG_CHUNK\"}");
        result = parser.parse("{\"totalProgress\":0}");
        result = parser.parse("{\"testCase\":[{\"name\":\"choice4\",\"value\":\"3\"},{\"name\":\"choice1\",\"value\":\"0\"},{\"name\":\"choice4\",\"value\":\"3\"}]}");
        result = parser.parse("{\"testCase\":[{\"name\":\"choice4\",\"value\":\"3\"},{\"name\":\"choice3\",\"value\":\"2\"},{\"name\":\"choice1\",\"value\":\"0\"}]}");
        result = parser.parse("{\"testCase\":[{\"name\":\"choice4\",\"value\":\"3\"},{\"name\":\"choice4\",\"value\":\"3\"},{\"name\":\"choice2\",\"value\":\"1\"}]}");
        result = parser.parse("{\"status\":\"END_CHUNK\"}");
        result = parser.parse("{\"status\":\"END_DATA\"}");

        System.out.println("end");
    }

    @Test
    @DisplayName("Manual structure")
    void manualStructure() {
        var dataSession = DataSession.create(
                DataSessionConnection.get(
                        ConfigDefault.GENERATOR_ADDRESS_DEVELOP,
                        Path.of(ConfigDefault.KEYSTORE_DEVELOP),
                        ConfigDefault.KEYSTORE_PASSWORD
                ),
                ConfigDefault.MODEL_DEVELOP, ConfigDefault.F_STRUCTURE, TypeGenerator.Random);

        dataSession.getInitializer().source(Source.class);

        var parser = Factory.getChunkParserStream(dataSession);

        Optional<Object[]> result;

        result = parser.parse("{\"status\":\"BEG_DATA\"}");
        result = parser.parse("{\"info\":\"{'method':'TestStructure.generate(Data, int)','testSessionId':'00dc385f712da97a4ab9ba63e881158171a252c78865cf25dc7f5766675936b4','timestamp':'1678963446'}\"}");
        result = parser.parse("{\"info\":\"{'signature':'Person(String, int)'}\"}");
        result = parser.parse("{\"info\":\"{'signature':'Data(Person, int)'}\"}");
        result = parser.parse("{\"status\":\"BEG_CHUNK\"}");
        result = parser.parse("{\"totalProgress\":0}");
        result = parser.parse("{\"testCase\":[" +
                "{\"name\":\"choice1\",\"value\":\"Patryk\"}," +
                "{\"name\":\"choice1\",\"value\":\"5\"}," +
                "{\"name\":\"choice1\",\"value\":\"0\"}," +
                "{\"name\":\"choice1\",\"value\":\"2000\"}]}");
        result = parser.parse("{\"status\":\"END_CHUNK\"}");
        result = parser.parse("{\"status\":\"END_DATA\"}");

        System.out.println("end");
    }
}
