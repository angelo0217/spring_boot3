package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockExDividendDataDTO;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockMainTrendDataDTO;
import com.example.demo.entity.dto.StockWantgoo;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("CalculateMainTrendDataTask")
@Slf4j
public class CalculateWantgooDataTask implements Runnable {

    private final StockDayInfoService stockDayInfoService;
    private final LineNotifyService lineNotifyService;
    private final StockInfoService stockInfoService;
    private final int basePer = 10;

    public CalculateWantgooDataTask(
            StockDayInfoService stockDayInfoService,
            LineNotifyService lineNotifyService,
            StockInfoService stockInfoService
    ) {
        this.stockDayInfoService = stockDayInfoService;
        this.lineNotifyService = lineNotifyService;
        this.stockInfoService = stockInfoService;
    }

    public String getAgentMainPowerReasonUp(
            List<StockMainTrendDataDTO> ary
    ) {
        int baseCnt = 3;
        if (ary.size() >= baseCnt) {
            try {
                var average = ary.stream().limit(baseCnt)
                                 .mapToDouble(StockMainTrendDataDTO::getClose)
                                 .average().orElseThrow();

                double[] filtered = ary.stream()
                                       .limit(baseCnt)
                                       .filter(v -> v.getStockAgentMainPower() > 50)
                                       .filter(v -> v.getSkp5() > 1)
                                       .filter(v -> {
                                           var diff = Math.abs(v.getClose() - average);
                                           double percentage = diff / v.getClose();
                                           System.out.println("percentage >>>>>>" + percentage);
                                           return percentage < 0.03;
                                       })
                                       .mapToDouble(StockMainTrendDataDTO::getClose)
                                       .toArray();

                if (filtered.length == baseCnt) {
                    return "主力持續買進，價格暫無波動";
                }
            } catch (Exception e) {
                log.error("getAgentMainPowerReasonUp error: ", e);
            }
        }
        return null;
    }

    public String getAgentMainPowerReason(
            StockInfoDTO todayStock,
            List<StockInfoDTO> lastDayDataList,
            List<StockMainTrendDataDTO> ary
    ) {
        var yesTodayData = lastDayDataList.get(0);
        if (todayStock.getClose() < yesTodayData.getClose()) {
            return null;
        }

        if (yesTodayData.getClose() > yesTodayData.getOpen()) {
            return null;
        }

        var twoDayAgoData = lastDayDataList.get(1);
        if (yesTodayData.getClose() > twoDayAgoData.getClose()) {
            return null;
        }

        if (ary == null || ary.size() == 0) {
            return null;
        }

        var today = ary.get(0);
        var yesToday = ary.get(1);
        var twoDayAgo = ary.get(2);
        var max = ary.stream().limit(10).mapToDouble(StockMainTrendDataDTO::getClose).max().orElse(0.0);
        String role = null;
        if (
                twoDayAgo.getClose() >= yesToday.getClose()
                        && today.getClose() > yesToday.getClose()
        ) {
            if (today.getStockAgentMainPower() < -3 && yesToday.getStockAgentMainPower() < -3 &&
                today.getClose() > twoDayAgoData.getClose()
            ) {
                role = "符合兩日交易量負成長，收盤漲";
            }
        }
        return role;
    }

    public Double getReasonablePrice(String stockCode, StockWantgoo<StockExDividendDataDTO[], StockMainTrendDataDTO[]> wantgoo){

        if (!stockInfoService.containsETFCode(stockCode)) {
            if (wantgoo.getPbr() > 1.1 || wantgoo.getPer() < 5 || wantgoo.getPer() > 15)
                throw new RuntimeException("not match pr or pbr");
        }

        Map<String, Double> dividendByYear = new HashMap<>();

        var dataList = Arrays.asList(wantgoo.getReasonablePriceData());
        for (StockExDividendDataDTO d: dataList){
            if (dividendByYear.get(d.getYear()) == null)
                dividendByYear.put(d.getYear(), d.getCashDividend());
            else {
                var before =  dividendByYear.get(d.getYear());
                dividendByYear.put(d.getYear(), d.getCashDividend() + before);
            }
        }


        double totalDividend = 0;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int baseYear = currentYear - 1;

        for (int i = 0; i < 5; i++){
            var d = dividendByYear.get(String.valueOf(baseYear));
            if (d == null)
                throw new IllegalStateException("No consecutive 5 years found");
            totalDividend += d;
            baseYear -= 1;
        }

        double average = totalDividend / 5;

        return average * basePer;
    }

    public boolean calculatePerPbr(StockWantgoo<StockExDividendDataDTO[], StockMainTrendDataDTO[]> wantgoo,
            StockInfoDTO todayStock, WatchStockDTO watch
    ) {
        try {

            var reasonablePrice = this.getReasonablePrice(todayStock.getStockCode(), wantgoo);
            var lowerReasonablePrice = reasonablePrice * 0.9;

            if (lowerReasonablePrice > todayStock.getClose()) {
                lineNotifyService.sendReasonablePrice(watch, reasonablePrice, lowerReasonablePrice, basePer);
                return  true;
            }
        } catch (Exception ex) {
            log.error("{} call api error", todayStock.getStockCode(), ex);
        }
        return false;
    }


    public void callSingleStock(StockInfoDTO todayStock) {
        try {
            var watch =
                    WatchStockDTO.builder().stockCode(todayStock.getStockCode())
                                 .stockName(todayStock.getStockName())
                                 .detectVolumes(todayStock.getVolume())
                                 .detectMoney(todayStock.getClose())
                                 .lastDateMoney(null)
                                 .lastDayVolumes(null)
                                 .happenDate(LocalDateTime.now()).is_rise(false).build();

            var wantgoo = stockInfoService.getWantgooData(todayStock.getStockCode());

            if (this.calculatePerPbr(wantgoo, todayStock, watch))
                return;

            var lastDayDataList =
                    stockDayInfoService.getBeforeData(todayStock.getStockCode(), LocalDateTime.now(), 2);

            var mainTrendAry = Arrays.asList(wantgoo.getMainTrendData());

            var role = this.getAgentMainPowerReasonUp(mainTrendAry);
//            if (role == null) {
//                role = this.getAgentMainPowerReason(todayStock, lastDayDataList, mainTrendAry);
//            }
            if (role != null) {

                lineNotifyService.sendMainTrendData(watch, role);
            }


        } catch (Exception ex) {
            log.error("{} call api error", todayStock.getStockCode(), ex);
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
             .filter(v -> v.getVolume() > 1000)
//             .filter(v -> v.getClose() < v.getOpen())
//           .filter(v -> v.getStockCode().equals("0056"))
                .filter(v -> !stockInfoService.containsETFCode(v.getStockCode()))
             .forEach(this::callSingleStock);
//                .forEach(System.out::println);
    }
}
