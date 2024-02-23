package com.example.demo.utils;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.service.StockDayInfoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockUtils {

    public static boolean isEStock(String stockCode) {
        String twseRegex = "\\d{4}$";
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
                          System.out.println(v);
                          return v.getClose() < v.getOpen();
                      })
                      .count();

        return cnt == days;
    }
}
