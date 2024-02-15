package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Scope("prototype")
@Component("StockInfoAPITask")
@Slf4j
public class StockInfoAPITask implements Runnable{

    private StockDayInfoService stockDayInfoService;

    public StockInfoAPITask(StockDayInfoService stockDayInfoService){
        this.stockDayInfoService = stockDayInfoService;
    }

    private String apiUrl = "https://www.wantgoo.com/investrue/all-quote-info";

    public List<StockInfoDTO> getStockInfo() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\driver\\chromedriver.exe");

        // 初始化 Chrome 瀏覽器
        WebDriver driver = new ChromeDriver();

        try {
            // 打開目標網頁
            driver.get(apiUrl);
            String pageSource = driver.getPageSource();
            String regex = "<(.*?)>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageSource);
            String result = matcher.replaceAll("");
            StockInfoDTO[] stockInfoDTOS = JsonUtil.jsonToObject(result, StockInfoDTO[].class);
            return Arrays.asList(stockInfoDTOS);
        } finally {
            // 關閉瀏覽器
            driver.quit();
        }
    }

    @Override
    public void run() {
        var dataDate = stockDayInfoService.getMaxDataDate();
        String dataDateStr = dataDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!now.equals(dataDateStr)) {
            var cnt = this.stockDayInfoService.getDataDateCnt(now);
            if (cnt > 0)
                log.info("----------- data exist");
            else {
                log.info("==============================start stock task");
                List<StockInfoDTO> stockInfoDTOS = this.getStockInfo();
                var newList = stockInfoDTOS.stream()
                        .filter(e -> e.getClose() > StockConst.MIN_CLOSE && e.getClose() <= StockConst.MAX_CLOSE)
                        .collect(Collectors.toList());
                this.stockDayInfoService.saveAll(newList);
            }
        }
    }
}
