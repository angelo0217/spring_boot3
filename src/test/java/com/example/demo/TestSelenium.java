package com.example.demo;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.WatchStockService;
import com.example.demo.utils.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSelenium {

    @Autowired
    private StockDayInfoService stockDayInfoService;

    @Autowired
    private WatchStockService watchStockService;

    @Test
    public void writeWatch() {
        var watch = WatchStockDTO.builder()
                .stockCode("123")
                .detectVolumes(111233)
                .detectMoney(11111.1)
                .lastDateMoney(111233.1)
                .lastDayVolumes(12331)
                .happenDate(LocalDateTime.now()).build();

        watchStockService.create(watch);
    }

    @Test
    public void testSql() {
        var traceDate = stockDayInfoService.getMaxTraceDate();
        System.out.println("......" + traceDate);
    }

    @Test
    public void testTodayData() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        var cnt = this.stockDayInfoService.getDataDateCnt(now);
        System.out.println("--------------"+cnt);
        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE, StockConst.B_MAX_CLOSE, LocalDateTime.now());
        infos.stream().forEach(System.out::println);

    }


    @Test
    public void callSingleStock() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:2609:STOCK&quote=1";

        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

        System.out.println(jsonResponse);
        StockSingleInfoDTO info = JsonUtil.jsonToObject(jsonResponse, StockSingleInfoDTO.class);
        System.out.println(info);
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 設定 API 網址
//        String apiUrl = "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:2609:STOCK&quote=1";
//// 設定 Request Headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("User-Agent", "Your-User-Agent"); // 設定使用者代理
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        // 發送 HTTP GET 請求
//        StockSingleInfoDTO apiResponse = restTemplate.getForObject(apiUrl, StockSingleInfoDTO.class);
//
//        System.out.println(apiResponse);
    }

    @Test
    public void testRun() {
//        https://googlechromelabs.github.io/chrome-for-testing/#stable
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\dirver\\chromedriver.exe");

        // 初始化 Chrome 瀏覽器
        WebDriver driver = new ChromeDriver();

        try {
            // 打開目標網頁
            driver.get("https://www.wantgoo.com/investrue/all-quote-info");

            // 在這裡可以模擬進一步的互動，如點擊按鈕或輸入表單

            // 獲取網頁內容
            String pageSource = driver.getPageSource();

            String regex = "<(.*?)>";

            // 編譯正則表達式
            Pattern pattern = Pattern.compile(regex);

            // 創建 Matcher 對象
            Matcher matcher = pattern.matcher(pageSource);

            String result = matcher.replaceAll("");

            // 輸出過濾後的內容
            System.out.println("過濾後的內容: " + result.substring(0, 500));

            StockInfoDTO[] array = JsonUtil.jsonToObject(result, StockInfoDTO[].class);

            List<StockInfoDTO> stockInfoDTOS = Arrays.asList(array);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            var newList = stockInfoDTOS.stream().filter(e -> e.getOpen() > 10.0 && e.getOpen() <= 35.0).collect(Collectors.toList());
            System.out.println(this.stockDayInfoService.getDataDateCnt(now));
//            for(StockInfoDTO a: newList){
//                System.out.println(a);
//            }
//            this.stockDayInfoService.saveAll(newList);
        } finally {
            // 關閉瀏覽器
            driver.quit();
        }
    }
}
