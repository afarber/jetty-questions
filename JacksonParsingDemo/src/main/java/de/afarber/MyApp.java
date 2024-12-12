package de.afarber;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

// run at the command line with: mvn exec:java

public class MyApp {
    public static void main(String args[]) throws Exception {
        // String jsonStr = "{\"brand\":\"ford\", \"doors\":5}";
        String jsonStr = "{\"mycar\":{\"brand\":\"ford\", \"doors\":null}}";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        Map<String, Car> jsonMap = objectMapper.readValue(jsonStr,
                new TypeReference<Map<String, Car>>() {
                });

        System.out.println("jsonStr = " + jsonStr);
        System.out.println("jsonMap = " + jsonMap);
    }
}
