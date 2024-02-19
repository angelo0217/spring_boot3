package com.example.demo.service;

import com.example.demo.constant.StockConst;
import com.example.demo.constant.StockConst.REASON;
import com.example.demo.entity.dto.WatchStockDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LineNotifyService {

    private RestTemplate restTemplate;

    LineNotifyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void send(WatchStockDTO watch, StockConst.REASON reason) {
        var wording = (
                reason.equals(REASON.ABOUT_TO_OPEN_UP) || reason.equals(REASON.ABOUT_TO_OPEN_UP_DAY2)
        ) ? reason.getDescription() : String.format(
                reason.getDescription(),
                (watch.getDetectVolumes() / watch.getLastDayVolumes()),
                ((watch.getDetectMoney() - watch.getLastDateMoney()) / watch.getLastDateMoney() * 100)
        );
        String url = "https://notify-api.line.me/api/notify";
        String message = String.format(
                "\n 代號: %s \n 現在金額: %.2f \n 交易量: %d \n 前一日收盤金額: %.2f \n 前一日交易量: %d \n 漲停: %s \n 符合條件: %s",
                watch.getStockCode(), watch.getDetectMoney(), watch.getDetectVolumes(), watch.getLastDateMoney(),
                watch.getLastDayVolumes(), watch.is_rise(), wording
        );
        String token = "8ELnpvmW5yabcxMuTpCXq3NIuTMChs4AfHUjm1QOMUiCG";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "message=" + message;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        log.info("Response Code : " + response.getStatusCode());
    }
}
