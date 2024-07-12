package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("CalculateRSITask")
@Slf4j
public class CalculateRSITask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final LineNotifyService lineNotifyService;
    private final StockInfoService stockInfoService;

    public CalculateRSITask(
            StockDayInfoService stockDayInfoService,
            LineNotifyService lineNotifyService,
            StockInfoService stockInfoService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.lineNotifyService = lineNotifyService;
        this.stockInfoService = stockInfoService;
    }

    public static double calculateRSI(double[] closingPrices, int period) {
        double[] gains = new double[closingPrices.length];
        double[] losses = new double[closingPrices.length];

        // 计算每日价格变动
        for (int i = 1; i < closingPrices.length; i++) {
            double change = closingPrices[i] - closingPrices[i - 1];
            double roundedValue =Math.round(change * 100.0) / 100.0;

            if (roundedValue > 0) {
                gains[i] = roundedValue;
                losses[i] = 0;
            } else {
                gains[i] = 0;
                losses[i] = -roundedValue;
            }
        }

        // 计算初始的平均上涨和下跌
        double avgGain = 0;
        double avgLoss = 0;
        for (int i = 1; i <= period; i++) {
            avgGain += gains[i];
            avgLoss += losses[i];
        }
        avgGain /= period;
        avgLoss /= period;

        // 计算 RSI
        double rs;
        if (avgLoss == 0) {
            rs = Double.MAX_VALUE;
        } else {
            rs = avgGain / avgLoss;
        }
        double roundedRs =Math.round(rs * 100.0) / 100.0;

        double rsi;
        if (avgLoss == 0) {
            rsi = 100; // 连续上涨的情况下 RSI 接近 100
        } else if (avgGain == 0) {
            rsi = 0; // 连续下跌的情况下 RSI 接近 0
        } else {
            rsi = 100 - (100 / (1 + roundedRs));
        }
        return rsi;
    }

    public double calculateRSI(StockInfoDTO stockInfoDTO, int period) {
        var day = stockInfoDTO.getDataDate().plusDays(1);
        var dataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(),
                day, period + 1
        );

        Collections.reverse(dataList);

        double[] doubleValues = dataList.stream()
                                      .mapToDouble(v -> v.getClose())
                                      .toArray();

        return this.calculateRSI(doubleValues, period);
    }

    public String matchRSILogic(StockInfoDTO stockInfoDTO) {
        if((stockInfoDTO.getClose() - stockInfoDTO.getOpen()) / stockInfoDTO.getOpen() > 0.09)
            return null;

        int volumeDay = 20;

        var dataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(),
                stockInfoDTO.getDataDate(), volumeDay
        );

        var yesToday = dataList.get(0);

        var yesTodayMa5 = StockUtils.getMaDayMoney(stockDayInfoService, yesToday.getStockCode(),
                yesToday.getDataDate(), 5
        );

        var yesTodayMa20 = StockUtils.getMaDayMoney(stockDayInfoService, yesToday.getStockCode(),
                yesToday.getDataDate(), 20
        );

        var ma5 =  StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                stockInfoDTO.getDataDate(), 5
        );

        var ma20 =  StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                stockInfoDTO.getDataDate(), 20
        );

        if (ma5 < ma20 || yesTodayMa5 > yesTodayMa20)
            return null;

        var rsi = this.calculateRSI(stockInfoDTO, 10);
        var yesTodayRsi = this.calculateRSI(yesToday, 10);
        if (rsi < 30 || rsi > 70 || yesTodayRsi > rsi || (rsi > 50 && yesTodayRsi > 50))
            return null;


        var totalVolume = dataList.stream().mapToInt(value -> value.getVolume().intValue()).sum();

        double avgVolume = totalVolume / volumeDay;

        if (stockInfoDTO.getVolume().doubleValue() / avgVolume > 2) {
            System.out.println("rsi: " + rsi +  ",yesTodayRsi: " +yesTodayRsi+ ", avgVolume: " + avgVolume);
            return "rsi, ma, volume data 指標達標";
        }
        return null;
    }

    public void callSingleStock(StockInfoDTO todayStock) {
        try {
            var reason = this.matchRSILogic(todayStock);
            if (reason != null) {
                var watch =
                        WatchStockDTO.builder().stockCode(todayStock.getStockCode())
                                     .stockName(todayStock.getStockName())
                                     .detectVolumes(todayStock.getVolume())
                                     .detectMoney(todayStock.getClose())
                                     .lastDateMoney(null)
                                     .lastDayVolumes(null)
                                     .happenDate(LocalDateTime.now()).is_rise(false).build();
                lineNotifyService.sendRsiData(watch, reason);
                System.out.println(reason);
                System.out.println(watch);
            }
        } catch (Exception ex) {
//            log.error("", ex);
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
//             .filter(v -> v.getClose() > v.getOpen())
//             .filter(v -> v.getVolume() > 200)
             .filter(v -> v.getVolume() > 1000)
             .filter(v -> !stockInfoService.containsETFCode(v.getStockCode()))
//                .filter(v -> v.getStockCode().equals("4113"))
             .forEach(this::callSingleStock);
    }
}
