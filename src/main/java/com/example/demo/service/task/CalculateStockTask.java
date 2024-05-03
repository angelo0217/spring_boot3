package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.constant.StockConst.REASON;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockCacheService;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.service.db.WatchStockService;
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
    private final StockInfoService stockInfoService;

    public CalculateStockTask(
            StockDayInfoService stockDayInfoService,
            WatchStockService watchStockService,
            LineNotifyService lineNotifyService,
            StockCacheService stockCacheService,
            StockInfoService stockInfoService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
        this.stockCacheService = stockCacheService;
        this.lineNotifyService = lineNotifyService;
        this.stockInfoService = stockInfoService;
    }

    public boolean willToRise(StockInfoDTO stockInfoDTO, StockSingleInfoDTO info) {
        if ((info.getRealTimePrice() - stockInfoDTO.getClose()) / stockInfoDTO.getClose() < 0.01) {
            return false;
        }

        var day = LocalDateTime.now();
        var ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                day, 5
        );
        Double ma20;
//        var ma20 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
//                day, 20
//        );

        if (info.getRealTimePrice() < ma5) {
            return false;
        }

        for (int i = 0; i <= 2; i++) {
            var infos = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(), day, 1);
            var data = infos.get(0);
            ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                    day, 5
            );
            ma20 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                    day, 20
            );
            if (data.getClose() > ma5 || data.getClose() > ma20) {
                return false;
            }
            day = infos.get(0).getDataDate();
        }
        return true;
    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {

        try {
            StockSingleInfoDTO info = stockInfoService.getStockInfo(stockInfoDTO.getStockCode());
            if (info == null) {
                return;
            }

            if (info.getRealTimePrice() != null) {
                if (info.getRealTimeVolume() < StockConst.DEFAULT_VOLUMES) {
                    return;
                }

                if (StockUtils.isStillRise(stockDayInfoService, stockInfoDTO.getStockCode(), 3)) {
                    return;
                }

                log.info("code: {}, volume: {}, total v: {}, last: {}, now: {}",
                        stockInfoDTO.getStockCode(),
                        stockInfoDTO.getVolume(), info.getRealTimeVolume(), stockInfoDTO.getClose(),
                        info.getRealTimePrice()
                );
                var watch = WatchStockDTO.builder()
                                         .stockCode(stockInfoDTO.getStockCode())
                                         .stockName(stockInfoDTO.getStockName())
                                         .detectVolumes(info.getRealTimeVolume())
                                         .detectMoney(info.getRealTimePrice())
                                         .lastDateMoney(stockInfoDTO.getClose())
                                         .lastDayVolumes(stockInfoDTO.getVolume())
                                         .happenDate(LocalDateTime.now())
                                         .is_rise(stockInfoDTO.isRise(info.getRealTimePrice())).build();
                var reason = REASON.NOTHING;

//                if (info.getRealTimePrice() > stockInfoDTO.getClose()
//                        && info.getRealTimePrice() > stockInfoDTO.getHigh()
//                        && stockInfoDTO.getClose() > stockInfoDTO.getOpen()) {
//                    reason = REASON.OVER_LAST_DAY_HIGH;
//                } else {

                if (willToRise(stockInfoDTO, info)) {
                    reason = REASON.TODAY_START_OVER_MA5;
                } else {
                    if (
                            StockUtils.isOverMA5logic(
                                    stockDayInfoService, stockInfoDTO.getStockCode(), info.getRealTimePrice()
                            ) && (info.getRealTimePrice() - stockInfoDTO.getClose()) / stockInfoDTO.getClose()
                                    > 0.01) {
                        reason = REASON.OVER_MA5;
                    } else {
                        reason = StockConst.REASON.getStockReason(
                                info.getRealTimeVolume(),
                                info.getRealTimePrice(),
                                stockInfoDTO
                        );
                    }
                }
//                }
                if (!reason.equals(StockConst.REASON.NOTHING)) {
//                    if (reason.equals(REASON.MAGNIFICATION_UP) || reason.equals(REASON.OVER_MA5)) {
//                        watchStockService.create(watch);
//                    }
                    var cache = stockCacheService.getWatchStock(stockInfoDTO.getStockCode());
                    if (cache == null) {
                        lineNotifyService.send(watch, reason);
                    }
                    stockCacheService.saveWatchStock(stockInfoDTO.getStockCode(), watch);
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
