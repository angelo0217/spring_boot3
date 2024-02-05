package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.WatchStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Scope("prototype")
@Component("CalculateStockTask")
@Slf4j
public class CalculateStockTask implements Runnable {

    private StockDayInfoService stockDayInfoService;
    private WatchStockService watchStockService;

    public CalculateStockTask(StockDayInfoService stockDayInfoService, WatchStockService watchStockService) {
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:" + stockInfoDTO.getStockCode() + ":STOCK&quote=1";

        try {
            ResponseEntity<StockSingleInfoDTO> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, StockSingleInfoDTO.class);
            if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
                StockSingleInfoDTO info = responseEntity.getBody();
                if (info.getData().getO() != null && info.getData().getO().size() > 0) {
                    Double realTimeValue = info.getData().getO().get(0);
                    if (realTimeValue != null) {
                        Integer volumes = info.getData().getV().stream()
                                .filter(value -> value != null)
                                .mapToInt(Double::intValue)
                                .sum();
                        log.info("code: {}, volume: {}, total v: {}, last: {}, now: {}",
                                stockInfoDTO.getStockCode(), stockInfoDTO.getVolume(), volumes
                                , stockInfoDTO.getClose(), realTimeValue);
                        if (volumes < StockConst.DEFAULT_VOLUMES)
                            return;

                        if ((volumes / stockInfoDTO.getVolume()) > StockConst.MAGNIFICATION && realTimeValue > stockInfoDTO.getClose()) {
                            boolean is_rise = (((realTimeValue - stockInfoDTO.getClose()) / stockInfoDTO.getClose()) > 0.085);
                            var watch = WatchStockDTO.builder()
                                    .stockCode(stockInfoDTO.getStockCode())
                                    .detectVolumes(volumes)
                                    .detectMoney(realTimeValue)
                                    .lastDateMoney(stockInfoDTO.getClose())
                                    .lastDayVolumes(stockInfoDTO.getVolume())
                                    .happenDate(LocalDateTime.now())
                                    .is_rise(is_rise)
                                    .build();

                            watchStockService.create(watch);
                            log.info(" === 偵測到股票: {} 可能出現交易量爆表", stockInfoDTO.getStockCode());
                        }
                    }
                }
            } else {
                log.error("{} call api error {}}", stockInfoDTO.getStockCode(), responseEntity.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("{} call api error", stockInfoDTO.getStockCode(), ex);
        }
    }

    private static boolean isEStock(String stockCode) {
        String twseRegex = "\\d{4}$";
        Pattern pattern = Pattern.compile(twseRegex);
        Matcher matcher = pattern.matcher(stockCode);
        return matcher.matches();
    }

//    private static boolean isTPEXStock(String stockCode) {
//        // 上櫃股票代码以 "5" 开头
//        String tpexRegex = "^5\\d{3}$";
//        Pattern pattern = Pattern.compile(tpexRegex);
//        Matcher matcher = pattern.matcher(stockCode);
//        return matcher.matches();
//    }

    @Override
    public void run() {
        var traceDate = stockDayInfoService.getMaxTraceDate();
        var infos = stockDayInfoService.getMatchInfoByClose(StockConst.MIN_VAL, StockConst.MAX_VAL, traceDate);
        infos.stream().filter(v -> !v.getStockCode().contains("&"))
                .filter(v -> isEStock(v.getStockCode()))
                        .forEach(this::callSingleStock);
//        Flux.fromIterable(infos)
//        .filter(v -> !v.getStockCode().contains("&"))
//        .filter(v -> isEStock(v.getStockCode()))
//        .subscribe(this::callSingleStock);
    }
}
