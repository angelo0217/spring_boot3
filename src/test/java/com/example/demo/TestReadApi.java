package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class TestReadApi {

    @Test
    public void getETFStockCode() {
        var restTemplate = new RestTemplate();
        String apiUrl =
                "https://www.twse.com.tw/rwd/zh/ETF/list?response=json&_=1714289542446";

        String result = restTemplate.getForObject(apiUrl, String.class);

        try {
            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(result);
            JsonNode dataArray = jsonNode.get("data");

            // Access index 1 of each data array
            System.out.println("String[] etfCode = {");
            for (JsonNode data : dataArray) {
                String valueAtIndex1 = data.get(1).asText();
                System.out.println("\"" + valueAtIndex1 + "\",");
            }
            System.out.println("};");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
