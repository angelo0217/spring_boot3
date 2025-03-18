package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockMainTrendDataDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
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

    public boolean checkMa(StockInfoDTO stockData) {
        try {
            var ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockData.getStockCode(),
                    stockData.getDataDate(), 5
            );
            var ma20 = StockUtils.getMaDayMoney(stockDayInfoService, stockData.getStockCode(),
                    stockData.getDataDate(), 20
            );
            var ma60 = StockUtils.getMaDayMoney(stockDayInfoService, stockData.getStockCode(),
                    stockData.getDataDate(), 60
            );
            if (ma5 <= ma20 || ma20 <= ma60) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean matchSpecialLogic2(StockInfoDTO stockInfoDTO) {
        if(stockInfoDTO.getVolume() < 5000)
            return false;

        var lastDayDataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(),
                stockInfoDTO.getDataDate(), 5
        );

        if (lastDayDataList.size() < 5) {
            return false;
        }

        if ((stockInfoDTO.getClose() - stockInfoDTO.getOpen()) / stockInfoDTO.getOpen() > 0.015) {
            return false;
        }

        if ((stockInfoDTO.getHigh() - stockInfoDTO.getClose()) / stockInfoDTO.getClose() < 0.02){
            return false;
        }

        if (stockInfoDTO.getHigh() - stockInfoDTO.getOpen() < stockInfoDTO.getOpen() - stockInfoDTO.getLow()){
            return false;
        }

        if (!this.checkMa(stockInfoDTO))
            return false;

//        var last = lastDayDataList.get(lastDayDataList.size() - 1);
//        if (last.getClose() >= stockInfoDTO.getClose()) {
//            return false;
//        }
//
//        for (int i = 0; i < lastDayDataList.size(); i ++) {
//            var stockData = lastDayDataList.get(i);
//            if (!this.checkMa(stockData))
//                return false;
//
//            if ((i == 0 || i == 2 || i == 3) && (stockData.getOpen() < stockData.getClose()))
//                return false;
//            else if ((i == 1 || i == 4) && (stockData.getOpen() > stockData.getClose()))
//                return false;
//        }
        return true;
    }

    public boolean matchSpecialLogic(StockInfoDTO stockInfoDTO) {
        if(stockInfoDTO.getOpen() < stockInfoDTO.getClose())
            return false;

        if(stockInfoDTO.getVolume() < 3000 || (stockInfoDTO.getClose() - stockInfoDTO.getPreviousClose()) / stockInfoDTO.getPreviousClose() > 0.06)
            return false;
        var lastDayDataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(),
                stockInfoDTO.getDataDate(), 5
        );

        var avg = lastDayDataList.stream().mapToInt(StockInfoDTO::getVolume).average().orElse(10000);

        if (stockInfoDTO.getVolume()/avg > 3.5 && lastDayDataList.get(0).getVolume() / avg < 1.5) {
            var priceDeal = stockInfoService.getPriceDeal(stockInfoDTO.getStockCode(), true);
            if(priceDeal == null || priceDeal.getDealOnAskPrice().doubleValue() / priceDeal.getDealOnBidPrice().doubleValue() > 1.1)
                return true;
        }

        return false;

    }
    public void callSingleStock(StockInfoDTO todayStock) {
        try {
            if (this.matchSpecialLogic(todayStock)) {
                var watch =
                        WatchStockDTO.builder().stockCode(todayStock.getStockCode())
                                     .stockName(todayStock.getStockName())
                                     .detectVolumes(todayStock.getVolume())
                                     .detectMoney(todayStock.getClose())
                                     .lastDateMoney(null)
                                     .lastDayVolumes(null)
                                     .happenDate(LocalDateTime.now()).is_rise(false).build();
//                lineNotifyService.sendSpecialData(watch);
                System.out.println(watch);
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
    @Override
    public void run() {
        log.info("--------------------------start close logic");
        var dataDate = stockDayInfoService.getMaxDataDate();

        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE,
                StockConst.B_MAX_CLOSE, dataDate
        );

        infos.stream().filter(v -> !v.getStockCode().contains("&"))
             .filter(v -> StockUtils.isEStock(v.getStockCode()))
             .filter(v -> v.getClose() > v.getOpen())
//             .filter(v -> v.getVolume() > 5000)
             .filter(v -> !stockInfoService.containsETFCode(v.getStockCode()))
//                .filter(v -> v.getStockCode().equals("1611"))
             .forEach(this::callSingleStock);
    }
}
