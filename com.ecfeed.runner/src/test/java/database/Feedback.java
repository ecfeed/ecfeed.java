package database;

import com.ecfeed.TestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Feedback {

    private static final String model = "QBE2-4EMV-K8GK-IUQ4-FSMS";
    private static final String method = "QuickStart.test";

    @Test
    @DisplayName("NWise")
    void exportNWise() {
        TestProvider testProvider = TestProvider.create(model);

        Map<String, Object> config = new HashMap<>();
//        config.put("feedback", "true");

        for (Object[] chunk : testProvider.generateNWise(method, config)) {
            System.out.println(Arrays.toString(chunk));
        }
    }
}
