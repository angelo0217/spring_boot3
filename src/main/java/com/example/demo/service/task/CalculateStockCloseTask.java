package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.constant.StockConst.REASON;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.StockSinglyService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("CalculateStockCloseTask")
@Slf4j
public class CalculateStockCloseTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final LineNotifyService lineNotifyService;
    private final StockSinglyService stockSinglyService;

    public CalculateStockCloseTask(
            StockDayInfoService stockDayInfoService,
            LineNotifyService lineNotifyService,
            StockSinglyService stockSinglyService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.lineNotifyService = lineNotifyService;
        this.stockSinglyService = stockSinglyService;
    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {
        if (!StockUtils.isKeepFall(stockDayInfoService, stockInfoDTO.getStockCode(), 2))
            return;

        try {
            StockSingleInfoDTO info = stockSinglyService.getStockInfo(stockInfoDTO.getStockCode());
            if (info != null) {
                System.out.println(info.getRealTimePrice());
                System.out.println(stockInfoDTO);
                if (info.getRealTimePrice() != null && info.getRealTimePrice() > stockInfoDTO.getClose()
                        && info.getRealTimePrice() - stockInfoDTO.getClose() < 3
                ) {
                    var watch =
                            WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
                                         .detectVolumes(info.getRealTimeVolume())
                                         .detectMoney(info.getRealTimePrice()).lastDateMoney(stockInfoDTO.getClose())
                                         .lastDayVolumes(stockInfoDTO.getVolume())
                                         .happenDate(LocalDateTime.now()).is_rise(false).build();
                    lineNotifyService.send(watch, String.format("連續跌 %d 天，今日小漲，且今日交易量大於昨日，明日可能會漲", 2));
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
             .filter(v -> v.getClose() > v.getOpen())
             .forEach(this::callSingleStock);
    }
}
