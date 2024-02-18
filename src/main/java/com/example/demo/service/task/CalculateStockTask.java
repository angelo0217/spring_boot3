package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.constant.StockConst.REASON;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.StockCacheService;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.WatchStockService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Scope("prototype")
@Component("CalculateStockTask")
@Slf4j
public class CalculateStockTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final WatchStockService watchStockService;
    private final LineNotifyService lineNotifyService;
    private final StockCacheService stockCacheService;

    public CalculateStockTask(
            StockDayInfoService stockDayInfoService,
            WatchStockService watchStockService,
            LineNotifyService lineNotifyService, StockCacheService stockCacheService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
        this.lineNotifyService = lineNotifyService;
        this.stockCacheService = stockCacheService;
    }

    public StockSingleInfoDTO getStockInfo(String stockCode) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl =
                "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:" + stockCode
                        + ":STOCK&quote=1";

        ResponseEntity<StockSingleInfoDTO> responseEntity = restTemplate.exchange(apiUrl,
                HttpMethod.GET, null,
                StockSingleInfoDTO.class
        );
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
            var info = responseEntity.getBody();
            if (info.getData().getO() != null && info.getData().getO().size() > 0) {
                return info;
            }
        }
        log.error("failed to call api {}", responseEntity);
        throw new RuntimeException("no data");
    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {

        try {
            StockSingleInfoDTO info = getStockInfo(stockInfoDTO.getStockCode());

            var data = stockDayInfoService.getBeforeData(info.getStatusCode(), LocalDateTime.now(),
                    StockConst.RISE_REF_DATE
            );
            var cnt = data.stream()
                          .filter(v -> (v.getClose() - v.getOpen()) / v.getOpen() > 0.05
                                  && v.getVolume() > StockConst.DEFAULT_VOLUMES)
                          .count();

            if (cnt == StockConst.RISE_REF_DATE) {
                log.info("{}, 3日內漲幅超過5% 2次以上", info.getStatusCode());
                return;
            }

            Double realTimePrice = info.getData().getO().get(0);
            if (realTimePrice != null) {
                Integer volumes = info.getData().getV().stream().filter(value -> value != null)
                                      .mapToInt(Double::intValue)
                                      .sum();
                if (volumes < StockConst.DEFAULT_VOLUMES) {
                    return;
                }

                log.info("code: {}, volume: {}, total v: {}, last: {}, now: {}",
                        stockInfoDTO.getStockCode(),
                        stockInfoDTO.getVolume(), volumes, stockInfoDTO.getClose(), realTimePrice
                );

                var reason = StockConst.REASON.getStockReason(volumes, stockInfoDTO.getVolume(),
                        realTimePrice,
                        stockInfoDTO.getClose()
                );
                if (reason.equals(StockConst.REASON.MAGNIFICATION_UP) || reason.equals(REASON.MAGNIFICATION_DOWN)) {
                    boolean is_rise = (
                            ((realTimePrice - stockInfoDTO.getClose()) / stockInfoDTO.getClose())
                                    > 0.09);
                    var watch = WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode()).detectVolumes(volumes)
                                             .detectMoney(realTimePrice).lastDateMoney(stockInfoDTO.getClose())
                                             .lastDayVolumes(stockInfoDTO.getVolume())
                                             .happenDate(LocalDateTime.now()).is_rise(is_rise).build();

                    watchStockService.create(watch);
                    if (stockCacheService.getWatchStock(info.getStatusCode()) == null) {
                        lineNotifyService.send(watch, reason);
                    }
                    stockCacheService.saveWatchStock(watch.getStockCode(), watch);
                }
            }

        } catch (Exception ex) {
            log.error("{} call api error", stockInfoDTO.getStockCode(), ex);
        }
    }

    // private static boolean isTPEXStock(String stockCode) {
    // // 上櫃股票代码以 "5" 开头
    // String tpexRegex = "^5\\d{3}$";
    // Pattern pattern = Pattern.compile(tpexRegex);
    // Matcher matcher = pattern.matcher(stockCode);
    // return matcher.matches();
    // }

    @Override
    public void run() {
        var dataDate = stockDayInfoService.getMaxDataDate();
        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE,
                StockConst.B_MAX_CLOSE, dataDate
        );
        infos.stream().filter(v -> !v.getStockCode().contains("&"))
             .filter(v -> StockUtils.isEStock(v.getStockCode()))
             .forEach(this::callSingleStock);
        // Flux.fromIterable(infos)
        // .filter(v -> !v.getStockCode().contains("&"))
        // .filter(v -> isEStock(v.getStockCode()))
        // .subscribe(this::callSingleStock);
    }
}
