package com.example.demo.utils;

import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.service.db.StockDayInfoService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockUtils {

    public static boolean isEStock(String stockCode) {
        String twseRegex = "\\d{4,6}$";
        Pattern pattern = Pattern.compile(twseRegex);
        Matcher matcher = pattern.matcher(stockCode);
        return matcher.matches();
    }


    public static boolean isKeepFall(StockDayInfoService stockDayInfoService, String stockCode, int days) {
        var data = stockDayInfoService.getBeforeData(
                stockCode, LocalDateTime.now(), days
        );
        var cnt = data.stream()
                      .filter(v -> {
                          return v.getClose() < v.getOpen();
                      })
                      .count();

        return cnt == days;
    }

    public static Double convertToDouble(String val) {
        try {
            return Double.parseDouble(val);
        }catch (Exception ex) {
            return 0.0;
        }
    }

    public static Integer convertToInt(String val) {
        try {
            return Integer.parseInt(val.replaceAll(",", ""))/ 1000;
        }catch (Exception ex) {
            return 0;
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy/MM/dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        int year = localDate.getYear() + 1911;
        LocalDate adjustedDate = LocalDate.of(year, localDate.getMonth(), localDate.getDayOfMonth());

        // 转换为 LocalDateTime
        LocalDateTime localDateTime = adjustedDate.atStartOfDay();
        return localDateTime;
    }

    public static boolean isOverMA5logic(StockDayInfoService stockDayInfoService, String stockCode, Double money) {
        var day = LocalDateTime.now();
        var checkMoney = money;
        var md5Map = new HashMap<Integer, Boolean>();
        for(int i = 0; i < 3; i++) {
            var infos = stockDayInfoService.getBeforeData(stockCode, day, 1);
            if (infos != null) {
                md5Map.put(i, isOverMaDay(stockDayInfoService, stockCode, checkMoney, day, 5));
                day = infos.get(0).getDataDate();
                checkMoney = infos.get(0).getClose();
            } else {
                return false;
            }
        }

        return md5Map.get(0) && !md5Map.get(1) && !md5Map.get(2);
    }

    public static boolean isStillRise(StockDayInfoService stockDayInfoService, String stockCode, Integer days) {
        var infos = stockDayInfoService.getBeforeData(stockCode, LocalDateTime.now(), days);
        var cnt = infos.stream().filter(v -> v.getClose() > v.getOpen()).count();
        return days == cnt;
    }

    public static Boolean isOverMaDay(StockDayInfoService stockDayInfoService, String stockCode, Double money,
            LocalDateTime day, int maDay) {
        var maMoney = getMaDayMoney(stockDayInfoService, stockCode, day, maDay);
        return money > maMoney;
    }

    public static Double getMaDayMoney(StockDayInfoService stockDayInfoService, String stockCode,
            LocalDateTime day, int maDay) {
        var infos = stockDayInfoService.getBeforeData(stockCode, day.plusDays(1), maDay);
        if (infos.size() == maDay) {
            return  infos.stream().mapToDouble(StockInfoDTO::getClose).sum() / maDay;
        } else {
            throw new RuntimeException(stockCode + "not get data" + maDay);
        }
    }
}
