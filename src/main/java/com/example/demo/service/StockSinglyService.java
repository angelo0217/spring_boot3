package com.example.demo.service;

import com.example.demo.entity.dto.StockSingleInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class StockSinglyService {
    public StockSingleInfoDTO getStockInfo(String stockCode) {
        var restTemplate = new RestTemplate();
        String apiUrl =
                "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:" + stockCode
                        + ":STOCK&quote=1";

        ResponseEntity<StockSingleInfoDTO> responseEntity = restTemplate.exchange(apiUrl,
                HttpMethod.GET, null,
                StockSingleInfoDTO.class
        );
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
            var info = responseEntity.getBody();
            if (info.getData().getO() != null && !info.getData().getO().isEmpty()) {
                return info;
            } else {
                return null;
            }
        }
        log.error("failed to call api {}", responseEntity);
        throw new RuntimeException("no data");
    }
}
