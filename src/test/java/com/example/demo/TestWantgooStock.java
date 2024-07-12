package com.example.demo;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.db.StockDayInfo;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockNameDTO;
import com.example.demo.respository.StockDayInfoRepository;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.service.db.StockNameService;
import com.example.demo.utils.JsonUtil;
import com.example.demo.utils.StockUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestWantgooStock {

    @Autowired
    private StockNameService stockNameService;

    @Autowired
    private StockDayInfoService stockDayInfoService;

    @Autowired
    private StockInfoService stockInfoService;

    @Autowired
    private StockDayInfoRepository stockDayInfoRepository;

    @Test
    public void getAllStockName() {
        var stockInfos = this.stockNameService.findAll();

        System.out.println(stockInfos);
    }

    @Test
    public void getStockName() {
        System.setProperty("webdriver.chrome.driver", StockConst.CHROME_DRIVER);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setBinary("D:\\chrome-win64\\chrome.exe");
        // 初始化 Chrome 瀏覽器
        WebDriver driver = new ChromeDriver(options);

        try {
            // 打開目標網頁
            driver.get("https://www.wantgoo.com/investrue/all-alive");
            String pageSource = driver.getPageSource();
            String regex = "<(.*?)>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageSource);
            String result = matcher.replaceAll("");
            List<Map<String, Object>> stockNameList = JsonUtil.jsonToObject(result, List.class);
            var saveList = stockNameList.stream()
                    .map(v -> StockNameDTO.builder()
                            .stockName((String)v.get("name")).stockCode((String)v.get("id")).build()
                            ).collect(Collectors.toList());

            this.stockNameService.saveAll(saveList);
        } finally {
            // 關閉瀏覽器
            driver.quit();
        }

    }

    @Test
    public void patchAllData() {
        var tradeDate = stockDayInfoService.getMaxTraceDate();
        List<StockInfoDTO> stockInfoDTOS = this.stockInfoService.getStockInfoWithGoogleDriver();
        var newList = stockInfoDTOS.stream()
                                   .filter(v -> StockUtils.isEStock(v.getStockCode()))
                                   .filter(e -> e.getClose() > StockConst.MIN_CLOSE
                                           && e.getClose() <= StockConst.MAX_CLOSE)
                                   .collect(Collectors.toList());

        for(StockInfoDTO d: newList)
            patchData(d.getStockCode());
    }


    @Test
    public void patchSingleStockData(){
        List<String> resultList = stockDayInfoRepository.findStocksWithOneRecord();

        for (String stockCode : resultList) {
//            System.out.println(stockCode);
            patchData(stockCode);
        }
//        patchData("3322");
    }
//    @Test
    public void patchData(String stockCode) {
        System.setProperty("webdriver.chrome.driver", StockConst.CHROME_DRIVER);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setBinary("D:\\chrome-win64\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);

        try {
            // 打開目標網頁 https://www.wantgoo.com/stock/8054/technical-chart
            driver.get("https://www.wantgoo.com/investrue/"+stockCode+"/historical-daily-candlesticks?before=1718294400000&top"
                    + "=60");
            String pageSource = driver.getPageSource();
            String regex = "<(.*?)>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageSource);
            String result = matcher.replaceAll("");
            StockInfoDTO[] stockNameList = JsonUtil.jsonToObject(result, StockInfoDTO[].class);

            var newStream = Arrays.asList(stockNameList).stream()
                    .map(v -> {
                        Instant instant = Instant.ofEpochMilli(v.getTime());
                        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        v.setDataDate(dateTime);
                        v.setStockCode(stockCode);
                        return v;
                    }).collect(Collectors.toList());
//            System.out.println(newStream.get(0).getDataDate());
//            System.out.println(newStream.get(newStream.size() - 1).getDataDate());
//            stockDayInfoService.create(newStream.get(0));
            stockDayInfoService.saveAll(newStream);

        } catch (Exception ex) {
            System.out.println(ex);
        }finally {
            // 關閉瀏覽器
            driver.quit();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
