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
import java.time.LocalTime;
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

    public void callSingleStock(StockInfoDTO stockInfoDTO) {

        try {

            StockSingleInfoDTO info = stockInfoService.getStockInfo(stockInfoDTO.getStockCode());
            if (info == null) {
                return;
            }
            var lastDayDataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(),
                    LocalDateTime.now(), 5
            );

            LocalTime currentTime = LocalTime.now();
            LocalTime elevenAm = LocalTime.of(10, 30);
            // 比较当前时间和11:00时间
            double baseRange = 3;
            if (currentTime.isBefore(elevenAm)) {
                baseRange = 1.5;
            }

            var avg = lastDayDataList.stream().mapToInt(StockInfoDTO::getVolume).average().orElse(10000);

            if (info.getRealTimePrice() != null) {
                var cacheData = stockCacheService.getWatchStock(stockInfoDTO.getStockCode());
                if (cacheData != null)
                    return;

                if (info.getRealTimeVolume() / avg < baseRange || lastDayDataList.get(0).getVolume() / avg > baseRange
                        || info.getRealTimeVolume() < 2000) {
                    return;
                }
                if (info.getRealTimePrice() <= stockInfoDTO.getClose() ||
                        (info.getRealTimePrice() - stockInfoDTO.getClose()) / stockInfoDTO.getClose() > 0.06
                ) {
                    return;
                }

                var priceDeal = stockInfoService.getPriceDeal(stockInfoDTO.getStockCode(), false);
                if(priceDeal == null || priceDeal.getDealOnAskPrice().doubleValue() / priceDeal.getDealOnBidPrice().doubleValue() < 1.1)
                    return;
                log.info("**** code: {}, volume: {}, total v: {}, last: {}, now: {}",
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

//                lineNotifyService.sendMainTrendData(watch, "交易量超過去5日平均"+baseRange+"倍，可注意是否大量買入");
//                stockCacheService.saveWatchStock(stockInfoDTO.getStockCode(), watch);

                System.out.println(watch);
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
                30, dataDate
        );
        infos.stream().filter(v -> !v.getStockCode().contains("&"))
             .filter(v -> !stockInfoService.containsETFCode(v.getStockCode()))
//             .filter(v -> v.getStockCode().equals("1611"))
             .forEach(this::callSingleStock);
        // Flux.fromIterable(infos)
        // .filter(v -> !v.getStockCode().contains("&"))
        // .filter(v -> isEStock(v.getStockCode()))
        // .subscribe(this::callSingleStock);
    }
}
