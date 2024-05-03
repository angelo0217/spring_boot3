package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockCacheService;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("CalculateStockOpenTask")
@Slf4j
public class CalculateStockOpenTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final LineNotifyService lineNotifyService;
    private final StockCacheService stockCacheService;
    private final StockInfoService stockInfoService;

    public CalculateStockOpenTask(
            StockDayInfoService stockDayInfoService,
            LineNotifyService lineNotifyService,
            StockCacheService stockCacheService,
            StockInfoService stockInfoService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.stockCacheService = stockCacheService;
        this.lineNotifyService = lineNotifyService;
        this.stockInfoService = stockInfoService;
    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {

        try {
            StockSingleInfoDTO info = stockInfoService.getStockInfo(stockInfoDTO.getStockCode());
            if (info != null) {
                if (info.getRealTimePrice() != null) {
                    if (stockInfoDTO.isRise(info.getRealTimePrice())) {
                        return;
                    }

                    var ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                            LocalDateTime.now(), 5
                    );
                    if (stockInfoDTO.getClose() > ma5)
                        return;

                    var watch =
                            WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
                                         .stockName(stockInfoDTO.getStockName())
                                         .detectVolumes(info.getRealTimeVolume())
                                         .detectMoney(info.getRealTimePrice()).lastDateMoney(stockInfoDTO.getClose())
                                         .lastDayVolumes(stockInfoDTO.getVolume())
                                         .happenDate(LocalDateTime.now())
                                         .is_rise(stockInfoDTO.isRise(info.getRealTimePrice())).build();

                    var cacheInfo = stockCacheService.getSpecialWatchStock(stockInfoDTO.getStockCode());
                    if (cacheInfo != null) {
                        if (cacheInfo.getDetectMoney() < watch.getDetectMoney()
                                && watch.getDetectMoney() > stockInfoDTO.getClose()
                                && (watch.getDetectMoney() - stockInfoDTO.getClose()) / stockInfoDTO.getClose() > 0.02
                                && watch.getDetectVolumes() > 300
                        ) {
                            lineNotifyService.send(watch, "開盤 900-930，前3日跌，目前持續漲幅");
                        }
                    }
                    stockCacheService.saveSpecialWatchStock(stockInfoDTO.getStockCode(), watch);
                }
            }
        } catch (Exception ex) {
            log.error("{} call api error", stockInfoDTO.getStockCode(), ex);
        }
    }

    @Override
    public void run() {
        var dataDate = stockDayInfoService.getMaxDataDate();
        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE,
                StockConst.B_MAX_CLOSE, dataDate
        );
        infos.stream().filter(v -> !v.getStockCode().contains("&"))
             .filter(v -> StockUtils.isEStock(v.getStockCode()))
             .filter(v -> v.getClose() < v.getOpen())
             .filter(v -> StockUtils.isKeepFall(stockDayInfoService, v.getStockCode(), 3))
             .forEach(this::callSingleStock);
    }
}
