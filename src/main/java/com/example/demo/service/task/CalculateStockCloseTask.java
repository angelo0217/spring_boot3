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
@Component("CalculateStockCloseTask")
@Slf4j
public class CalculateStockCloseTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final LineNotifyService lineNotifyService;
    private final StockInfoService stockInfoService;

    public CalculateStockCloseTask(
            StockDayInfoService stockDayInfoService,
            LineNotifyService lineNotifyService,
            StockInfoService stockInfoService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.lineNotifyService = lineNotifyService;
        this.stockInfoService = stockInfoService;
    }

    public void aiLogic(StockInfoDTO stockInfoDTO){
        try {
            var now = LocalDateTime.now();
            var tomorrow = now.minusDays(1);
            var lastDayDataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(), tomorrow,
                    100
            );
            Collections.reverse(lastDayDataList);

//            List<Double> changes = new ArrayList<>();
//            for (StockInfoDTO data : lastDayDataList) {
//                double change = data.getClose() - data.getOpen();
//                changes.add(change);
//            }
//
//            // 計算每次漲之前的加權平均交易量和金額
//            List<Double> weightedVolumes = new ArrayList<>();
//            List<Double> weightedAmounts = new ArrayList<>();
//            double currentVolume = 0;
//            double currentAmount = 0;
//            for (int i = 0; i < changes.size(); i++) {
//                if (changes.get(i) > 0) {
//                    weightedVolumes.add(currentVolume);
//                    weightedAmounts.add(currentAmount);
//                    currentVolume = 0;
//                    currentAmount = 0;
//                } else {
//                    currentVolume += lastDayDataList.get(i).getVolume(); // 累加當天的交易量
//                    currentAmount += lastDayDataList.get(i).getOpen() * lastDayDataList.get(i).getVolume(); // 累加當天的交易金額
//                }
//            }
//
//            // 判斷隔天是否會漲
//            boolean nextDayUp = false;
//            if (weightedVolumes.size() > 0) {
//                double lastWeightedVolume = weightedVolumes.get(weightedVolumes.size() - 1);
//                double lastWeightedAmount = weightedAmounts.get(weightedAmounts.size() - 1);
//                double avgWeightedVolume = weightedVolumes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
//                double avgWeightedAmount = weightedAmounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
//                nextDayUp = lastWeightedVolume > avgWeightedVolume && lastWeightedAmount > avgWeightedAmount;
//                if (nextDayUp)
//                    System.out.println(stockInfoDTO.getStockCode() + "----將漲");
//            }



            List<Double> changes = new ArrayList<>();
            List<Integer> volumes = new ArrayList<>();
            for (StockInfoDTO data : lastDayDataList) {
                double change = data.getClose() - data.getOpen();
                changes.add(change);
                volumes.add((int) data.getVolume());
            }

            // 識別週期
            List<Integer> upPeriods = new ArrayList<>();
            List<Integer> downPeriods = new ArrayList<>();

            String currentTrend = changes.get(0) > 0 ? "up" : "down";
            int currentPeriod = 1;

            for (int i = 1; i < changes.size(); i++) {
                if ((changes.get(i) > 0 && currentTrend.equals("up")) || (changes.get(i) < 0 && currentTrend.equals(
                        "down"))) {
                    currentPeriod++;
                } else {
                    if (currentTrend.equals("up")) {
                        upPeriods.add(currentPeriod);
                    } else {
                        downPeriods.add(currentPeriod);
                    }
                    currentTrend = changes.get(i) > 0 ? "up" : "down";
                    currentPeriod = 1;
                }
            }

            // 計算週期頻率
            double upFrequency = (double) upPeriods.size() / changes.size();
            double downFrequency = (double) downPeriods.size() / changes.size();

            if (upFrequency > downFrequency) {
                var watch =
                        WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
                                     .stockName(stockInfoDTO.getStockName())
                                     .detectVolumes(stockInfoDTO.getVolume())
                                     .detectMoney(stockInfoDTO.getClose())
                                     .lastDateMoney(null)
                                     .lastDayVolumes(null)
                                     .happenDate(LocalDateTime.now()).is_rise(false).build();
                lineNotifyService.send(watch, "AI計算邏輯");
            }
        } catch (Exception ex){
            log.error("ai logic");
        }

    }

    public void callSingleStock(StockInfoDTO stockInfoDTO) {
        try {
            var lastDayDataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(), LocalDateTime.now(),
                    1
            );

            if (!lastDayDataList.isEmpty()) {
                var lastDayData = lastDayDataList.get(0);
                var watch =
                        WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
                                     .stockName(stockInfoDTO.getStockName())
                                     .detectVolumes(stockInfoDTO.getVolume())
                                     .detectMoney(stockInfoDTO.getClose())
                                     .lastDateMoney(lastDayData.getClose())
                                     .lastDayVolumes(lastDayData.getVolume())
                                     .happenDate(LocalDateTime.now()).is_rise(false).build();
//                if (StockUtils.isKeepFall(stockDayInfoService, stockInfoDTO.getStockCode(), 3) &&
//                        stockInfoDTO.getClose() < lastDayData.getClose()) {
////                    System.out.println(watch.getStockCode());
//                    lineNotifyService.send(watch, "連跌4日，可注意是否會回漲");
//                    return;
//                }

                if (
                        stockInfoDTO.getClose() > lastDayData.getClose()
                        && stockInfoDTO.getClose() > lastDayData.getHigh()
                        && lastDayData.getClose() > lastDayData.getOpen()
                        && lastDayData.getClose() < lastDayData.getHigh()
                        && (lastDayData.getHigh() - lastDayData.getClose()) / lastDayData.getClose() > 0.02) {
                    lineNotifyService.send(watch, REASON.OVER_LAST_DAY_HIGH.getDescription());
//                    log.info("====={}", watch.getStockCode());
                    return;
                }

                var day = stockInfoDTO.getDataDate();
                day = day.plusDays(1);

                for (int i = 0; i <= 4; i++) {
                    var infos = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(), day, 1);
                    var data = infos.get(0);
                    var ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                            day, 5
                    );

//                    var ma10 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
//                            day, 10
//                    );

                    var ma20 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                            day, 20
                    );
                    if (i > 0 && data.getClose() > ma20) {
                        return;
                    }

                    if (i == 0 && data.getClose() < ma5) {
                        return;
                    }

                    if (i > 0 && data.getClose() > ma5) {
                        return;
                    }

                    day = infos.get(0).getDataDate();
                }

                lineNotifyService.send(watch, "符合即將漲，今日超過5日線，連續4日小於5日線，且皆小於20日線");
//                log.info("====={}", watch.getStockCode());
            }
        } catch (Exception ex) {
            log.error("{} has error", stockInfoDTO.getStockCode(), ex);
        }
    }

//    public void callSingleStock(StockInfoDTO stockInfoDTO) {
//        LocalDateTime now = LocalDateTime.now();
//        var yes = now.plusDays(-1);
////        LocalDateTime tomorrow = now.plusDays(1);
//        var lastDayDataList = stockDayInfoService.getBeforeData(stockInfoDTO.getStockCode(), yes,
//                    10
//            );
//        if (lastDayDataList.size() < 2)
//            return;
//
//        var todayInfo = lastDayDataList.get(0);
//
//        var max = lastDayDataList.stream().mapToDouble(StockInfoDTO::getClose).max().orElse(0.0);
//        var min = lastDayDataList.stream().mapToDouble(StockInfoDTO::getClose).min().orElse(0.0);
//        double average = lastDayDataList.stream().mapToDouble(StockInfoDTO::getClose).average().orElse(0.0);
//
//        var maxNinSub = (max - min) / 2;
//        var maxMinCenter = maxNinSub + min;
//        var averageSubtractionMin = average - min;
////        System.out.println("------" + stockInfoDTO.getStockCode());
////        System.out.println(max);
////        System.out.println(min);
////        System.out.println(average);
////        System.out.println(maxMinCenter);
////        System.out.println(averageSubtractionMin);
////        System.out.println(maxNinSub);
//
//        if (average > maxMinCenter && averageSubtractionMin > maxNinSub
//        && todayInfo.getClose() > averageSubtractionMin){
//            var lastDay =  lastDayDataList.get(1);
//            var watch =
//                WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
//                             .stockName(stockInfoDTO.getStockName())
//                             .detectVolumes(stockInfoDTO.getVolume())
//                             .detectMoney(stockInfoDTO.getClose())
//                             .lastDateMoney(lastDay.getClose())
//                             .lastDayVolumes(lastDay.getVolume())
//                             .happenDate(LocalDateTime.now()).is_rise(false).build();
//            System.out.println(watch.getStockCode() + "--------------");
////            lineNotifyService.send(watch, "符合即將漲，今日超過5日線，連續4日小於5日線，且皆小於20日線");
//        }
//    }
    @Override
    public void run() {
        log.info("--------------------------start close logic");
        LocalDateTime now = LocalDateTime.now();
        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE,
                StockConst.B_MAX_CLOSE, now
        );
        infos.stream().filter(v -> !v.getStockCode().contains("&"))
             .filter(v -> StockUtils.isEStock(v.getStockCode()))
//             .filter(v -> v.getVolume() > 1000)
//                .filter(v -> v.getStockCode().equals("3322"))
             .forEach(this::aiLogic);
    }
}
