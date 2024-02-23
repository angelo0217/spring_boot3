package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.constant.StockConst.REASON;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.StockCacheService;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.StockSinglyService;
import com.example.demo.service.WatchStockService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("CalculateStockTask")
@Slf4j
public class CalculateStockTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final WatchStockService watchStockService;
    private final LineNotifyService lineNotifyService;
    private final StockCacheService stockCacheService;
    private final StockSinglyService stockSinglyService;

    public CalculateStockTask(
            StockDayInfoService stockDayInfoService,
            WatchStockService watchStockService,
            LineNotifyService lineNotifyService,
            StockCacheService stockCacheService,
            StockSinglyService stockSinglyService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
        this.stockCacheService = stockCacheService;
        this.lineNotifyService = lineNotifyService;
        this.stockSinglyService = stockSinglyService;
    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {

        try {
            StockSingleInfoDTO info = stockSinglyService.getStockInfo(stockInfoDTO.getStockCode());
            if (info == null) {
                return;
            }

            if (info.getRealTimePrice() != null) {
                if (info.getRealTimeVolume() < StockConst.DEFAULT_VOLUMES) {
                    return;
                }

                log.info("code: {}, volume: {}, total v: {}, last: {}, now: {}",
                        stockInfoDTO.getStockCode(),
                        stockInfoDTO.getVolume(), info.getRealTimeVolume(), stockInfoDTO.getClose(),
                        info.getRealTimePrice()
                );
                var watch = WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
                                         .detectVolumes(info.getRealTimeVolume())
                                         .detectMoney(info.getRealTimePrice())
                                         .lastDateMoney(stockInfoDTO.getClose())
                                         .lastDayVolumes(stockInfoDTO.getVolume())
                                         .happenDate(LocalDateTime.now())
                                         .is_rise(stockInfoDTO.isRise(info.getRealTimePrice())).build();

                var reason = StockConst.REASON.getStockReason(
                        info.getRealTimeVolume(),
                        stockInfoDTO.getVolume(),
                        info.getRealTimePrice(),
                        stockInfoDTO.getClose()
                );
                if (!reason.equals(StockConst.REASON.NOTHING)) {
                    if (reason.equals(REASON.MAGNIFICATION_UP)) {
                        watchStockService.create(watch);
                    }

                    if (stockCacheService.getWatchStock(stockInfoDTO.getStockCode()) == null) {
                        lineNotifyService.send(watch, reason);
                        stockCacheService.saveWatchStock(watch.getStockCode(), watch);
                    }
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
