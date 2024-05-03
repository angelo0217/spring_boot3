package com.example.demo.service.stock;

import com.example.demo.constant.StockConst;
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
        var wording = String.format(
                reason.getDescription(),
                (watch.getDetectVolumes() / watch.getLastDayVolumes()),
                ((watch.getDetectMoney() - watch.getLastDateMoney()) / watch.getLastDateMoney() * 100)
        );
        this.send(watch, wording);
    }

    public void send(WatchStockDTO watch, String reason) {;
        String message = String.format(
                "\n 代號: %s - %s \n 現在金額: %.2f \n 交易量: %d \n 前一日收盤金額: %.2f \n 前一日交易量: %d \n 漲停: %s \n 符合條件: %s\n",
                watch.getStockCode(), watch.getStockName(), watch.getDetectMoney(), watch.getDetectVolumes(),
                watch.getLastDateMoney(),
                watch.getLastDayVolumes(), watch.is_rise(), reason
        );
        this.sendMsg(message, watch.getStockCode());
    }

    public void sendMainTrendData(WatchStockDTO watch, String role) {
        String message = String.format(
                "\n 代號: %s - %s \n 現在金額: %.2f \n 交易量: %d \n 符合主力操盤，可能漲 \n 原因: %s\n",
                watch.getStockCode(), watch.getStockName(), watch.getDetectMoney(), watch.getDetectVolumes(), role
        );
        this.sendMsg(message, watch.getStockCode());
    }
    public void sendSpecialData(WatchStockDTO watch) {
        String message = String.format(
                "\n 代號: %s - %s \n 現在金額: %.2f \n 交易量: %d \n 可能漲 \n 原因: 偽股市六脈神劍\n",
                watch.getStockCode(), watch.getStockName(), watch.getDetectMoney(), watch.getDetectVolumes()
        );
        this.sendMsg(message, watch.getStockCode());
    }
    public void sendReasonablePrice(WatchStockDTO watch, Double reasonablePrice, Double lowerReasonablePrice, int basePer) {
//        System.out.println(watch.getStockCode() + "," + reason);
        String message = String.format(
                "\n 代號: %s - %s \n 現在金額: %.2f \n 5年平均合理價: %.2f (股利平均 * 計算本益比) \n 5年平均合理價9折: %.2f\n 計算本益比: %d \n "
                        + "目前金額以低於合理價9折 \n",
                watch.getStockCode(), watch.getStockName(), watch.getDetectMoney(), reasonablePrice,
                lowerReasonablePrice, basePer
        );
        this.sendMsg(message, watch.getStockCode());
    }

    public void sendMsg(String message, String stockCode){
        String url = "https://notify-api.line.me/api/notify";
        message = message + "https://www.wantgoo.com/stock/" +stockCode+ "/major-investors/main-trend";
        String token = "8ELnpvmW5yaxMuTpCXq3NIuTMChs4AfHUjm1QOMUiCG";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "message=" + message;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        log.info("Response Code : " + response.getStatusCode());
    }

}
