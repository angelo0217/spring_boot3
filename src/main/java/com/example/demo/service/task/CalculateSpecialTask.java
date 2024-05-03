package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.constant.StockConst.REASON;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("CalculateSpecialTask")
@Slf4j
public class CalculateSpecialTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final LineNotifyService lineNotifyService;
    private final StockInfoService stockInfoService;

    public CalculateSpecialTask(
            StockDayInfoService stockDayInfoService,
            LineNotifyService lineNotifyService,
            StockInfoService stockInfoService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.lineNotifyService = lineNotifyService;
        this.stockInfoService = stockInfoService;
    }

    public boolean matchSpecialLogic(StockInfoDTO stockInfoDTO) {

        var ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                LocalDateTime.now(), 5
        );
        var ma10 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                LocalDateTime.now(), 10
        );

        if(ma5 >= ma10)
            return false;
        System.out.println("++++"+stockInfoDTO.getStockCode() + ", ma5: " + ma5 + ", ma10: " + ma10);
        var lastDayDataList = stockDayInfoService.getBeforeData("3313", LocalDateTime.now(),
                10
        );

        for(int i = 0; i < lastDayDataList.size(); i++){
            StockInfoDTO d = lastDayDataList.get(i);
            if (i < 4) {
                if (d.getClose() > d.getOpen())
                    return false;
            } else {
                if (i == lastDayDataList.size() - 1)
                    return false;

                var before = lastDayDataList.get(i + 1);
                if (d.getClose() > before.getClose()){
                    return true;
                } else
                    if (i > 5)
                        return false;
            }
        }
        return false;
    }

    public void callSingleStock(StockInfoDTO todayStock) {
        if (this.matchSpecialLogic(todayStock)) {
            var watch =
                    WatchStockDTO.builder().stockCode(todayStock.getStockCode())
                                 .stockName(todayStock.getStockName())
                                 .detectVolumes(todayStock.getVolume())
                                 .detectMoney(todayStock.getClose())
                                 .lastDateMoney(null)
                                 .lastDayVolumes(null)
                                 .happenDate(LocalDateTime.now()).is_rise(false).build();
            lineNotifyService.sendSpecialData(watch);
        }
    }
    @Override
    public void run() {
        log.info("--------------------------start close logic");
        LocalDateTime now = LocalDateTime.now();
        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE,
                StockConst.B_MAX_CLOSE, now
        );
        infos.stream().filter(v -> !v.getStockCode().contains("&"))
             .filter(v -> StockUtils.isEStock(v.getStockCode()))
                .filter(v -> v.getClose() > v.getOpen())
                .filter(v -> v.getVolume() > 30)
//             .filter(v -> v.getVolume() > 1000)
//                .filter(v -> v.getStockCode().equals("3322"))
             .forEach(this::callSingleStock);
    }
}
