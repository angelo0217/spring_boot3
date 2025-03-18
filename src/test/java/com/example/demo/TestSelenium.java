package com.example.demo;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.respository.StockDayInfoRepository;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.db.WatchStockService;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockCacheService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.service.task.CalculateRSITask;
import com.example.demo.service.task.CalculateSpecialTask;
import com.example.demo.service.task.CalculateStockTask;
import com.example.demo.service.task.CalculateWantgooDataTask;
import com.example.demo.service.task.StockInfoAPITask;
import com.example.demo.utils.JsonUtil;
import com.example.demo.utils.StockUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSelenium {

    @Autowired
    private StockDayInfoService stockDayInfoService;

    @Autowired
    private WatchStockService watchStockService;

    @Autowired
    private LineNotifyService lineNotifyService;

    @Autowired
    private StockCacheService stockCacheService;

    @Autowired
    private StockInfoService stockInfoService;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private StockDayInfoRepository stockDayInfoRepository;

//
//    @Test
//    public void getMainTrendData(){
//        var ary = stockInfoService.getMainTrendData("2020");
//        if (ary.size() > 2){
//            var today = ary.get(0);
//            var yesToday = ary.get(1);
//
//            System.out.println(today);
//            System.out.println(yesToday);
//        }
//    }

    @Test
    public void getBeforeDate(){
        var dataDate = stockDayInfoService.getMaxDataDate();
        var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE,
                StockConst.B_MAX_CLOSE, dataDate
        );

        for (StockInfoDTO d: infos)
            System.out.println(d.getStockName());
    }

    @Test
    public void getPushLocation() {
        var data = stockDayInfoService.getBeforeData("3322", LocalDateTime.now(), 60);
        var map = new HashMap<Double, List<LocalDateTime>>();
        for (StockInfoDTO info : data) {
            double scale = Math.pow(10, 1);
            double point = Math.ceil(info.getHigh() * scale) / scale;
            if (map.get(point) == null) {
                map.put(point, new ArrayList<>());
            }
            var ary = map.get(point);
            ary.add(info.getDataDate());
        }
        for (Map.Entry<Double, List<LocalDateTime>> entry : map.entrySet()) {
            Double key = entry.getKey();
            List<LocalDateTime> dates = entry.getValue();
            System.out.println("Key: " + key);
            for (LocalDateTime date : dates) {
                System.out.println("Date: " + date);
            }
        }
        Double maxKey = null;
        int maxSize = 0;
        for (Map.Entry<Double, List<LocalDateTime>> entry : map.entrySet()) {
            Double key = entry.getKey();
            List<LocalDateTime> list = entry.getValue();
            int size = list.size();
            if (maxKey == null || size > maxSize) {
                maxKey = key;
                maxSize = size;
            }
        }

        System.out.println("Key with maximum list size: " + maxKey);
        System.out.println("Maximum list size: " + maxSize);
    }

    @Test
    public void runStockInfo() {
        var stockInfoAPITask = beanFactory.getBean(
                StockInfoAPITask.class, stockDayInfoService, stockInfoService
        );
        stockInfoAPITask.run();
    }

    @Test
    public void realTime() {
        var calculateStockTask = beanFactory.getBean(
                CalculateStockTask.class, stockDayInfoService, watchStockService, lineNotifyService,
                stockCacheService, stockInfoService
        );
        calculateStockTask.run();
    }

    @Test
    public void runDali() {
        var calculateCloseTask = beanFactory.getBean(
                CalculateWantgooDataTask.class, stockDayInfoService, lineNotifyService, stockInfoService
        );
        calculateCloseTask.run();
    }

    @Test
    public void runSpecial() {
        var calculateSpecialTask = beanFactory.getBean(
                CalculateSpecialTask.class, stockDayInfoService, lineNotifyService, stockInfoService
        );
        calculateSpecialTask.run();
    }
    @Test
    public void runRsi() {
        var calculateRsiTask = beanFactory.getBean(
                CalculateRSITask.class, stockDayInfoService, lineNotifyService, stockInfoService
        );
        calculateRsiTask.run();
    }

    @Test
    public void testMd5() {
//        var day = stockDayInfoService.getMaxDataDate();
//        var yesterday = day.minusDays(1);

//        LocalDateTime now = LocalDateTime.now();
//        DayOfWeek today = now.getDayOfWeek();
//        LocalDateTime lastSaturday;
//        if (today == DayOfWeek.SATURDAY) {
//            lastSaturday = now.minusDays(7);
//        } else {
//            int daysToSubtract = today.getValue() % 7 + 1; // 计算需要向前调整的天数
//            lastSaturday = now.minusDays(daysToSubtract);
//        }
//        System.out.println(lastSaturday);

//        var infos = stockDayInfoService.getBeforeData("6218", day, 5);

//        System.out.println(StockUtils.isOverMd5twice(stockDayInfoService, "6218", 35.0));

        var dd = String.format("aaaa", 1, 2, 3);
        System.out.println(dd);
    }

    @Test
    public void testFall() {
        System.out.println(StockUtils.isKeepFall(stockDayInfoService, "4707", 2));
    }


    @Test
    public void testBeforeData() {
        var data = stockDayInfoService.getBeforeData("2312", LocalDateTime.now(), 1);
        data.stream().forEach(System.out::println);

        var cnt = data.stream()
                      .filter(v -> v.getClose() < v.getOpen())
                      .count();
        System.out.println("================" + cnt);
    }

    @Test
    public void testReason() {
//        var reason = StockConst.REASON.getStockReason(1500, 1400,
//                80.0, 70.0);
//        if (!reason.equals(StockConst.REASON.NOTHING)) {
//            System.out.println("1234 " + reason.getDescription());
//        } else {
//            System.out.println("1234");
//        }
    }

    @Test
    public void testCache() {
        var stockCode = "8110";
        for (int i = 0; i < 5; i++) {
            if (stockCacheService.getWatchStock(stockCode) == null) {
//                System.out.println(stockCacheService.getWatchStock(stockCode));
//                var watch = WatchStockDTO.builder()
//                                         .stockCode(stockCode)
//                                         .detectVolumes(111233)
//                                         .detectMoney(11111.12).lastDateMoney(111233.12)
//                                         .lastDayVolumes(12331)
//                                         .happenDate(LocalDateTime.now()).build();
//                stockCacheService.saveWatchStock(stockCode, watch);
                System.out.println("save stock");
            } else {
                System.out.println(stockCacheService.getWatchStock(stockCode));
            }
        }
    }

    @Test
    public void writeWatch() {
        var watch = WatchStockDTO.builder()
                                 .stockName("Tet")
                                 .stockCode("8092")
                                 .detectVolumes(1500)
                                 .detectMoney(14.1).lastDateMoney(14.7)
                                 .lastDayVolumes(500)
                                 .happenDate(LocalDateTime.now()).build();

		lineNotifyService.send(watch, "123");
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
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\dirver\\chromedriver_bak.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setBinary("D:\\chrome-win64\\chrome.exe");
        // 初始化 Chrome 瀏覽器
        WebDriver driver = new ChromeDriver(options);

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

    @Test
    public void runSpecialLogic() {
        var code = "3498";
        var now = LocalDateTime.now();

        now = now.minusDays(7);
        System.out.println(now);

        var ma5 = StockUtils.getMaDayMoney(stockDayInfoService, code,
                now, 5
        );
        var ma10 = StockUtils.getMaDayMoney(stockDayInfoService, code,
                now, 10
        );
        System.out.println("ma5: " + ma5);
        System.out.println("ma10: " + ma10);
        if(ma5 >= ma10) {
            System.out.println("not match 1");
            return;
        }

        System.out.println("++++"+code + ", ma5: " + ma5 + ", ma10: " + ma10 + ","  );
        now = now.plusDays(1);
        var lastDayDataList = stockDayInfoService.getBeforeData(code, now,
                15
        );

        var stockInfoDTO = lastDayDataList.get(0);
        System.out.println(stockInfoDTO);
        for(int i = 1; i < lastDayDataList.size(); i++){
            StockInfoDTO d = lastDayDataList.get(i);
            System.out.println(d);
            if (i <= 4) {
                if (d.getClose() > d.getOpen()){
                    System.out.println("not match 2");
                    return;
                }
            } else {
                if (i == lastDayDataList.size() - 1){
                    System.out.println("not match 3");
                    return;
                }
                if (d.getClose() < d.getOpen())
                    continue;
                else if (d.getClose() == d.getOpen()){
                    System.out.println("not match 4");
                    return;
                }
                else {
                    var before = lastDayDataList.get(i + 1);
                    System.out.println(">.." + d.getClose());
                    System.out.println(">.." + before.getClose());
                    System.out.println((d.getClose() - stockInfoDTO.getClose()) / d.getClose());
                    if (d.getClose() > before.getClose() && ((d.getClose() - stockInfoDTO.getClose()) / d.getClose() <= 0.05)){
                        ma5 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                                d.getDataDate(), 5
                        );
                        ma10 = StockUtils.getMaDayMoney(stockDayInfoService, stockInfoDTO.getStockCode(),
                                d.getDataDate(), 10
                        );
                        System.out.println("ma5: "+ ma5);
                        System.out.println("ma10: "+ ma10);
                        if (ma5 < ma10){
                            System.out.println("====================ok");
                        return;
                        }
                        else{
                            System.out.println("not match 5");
                            return;
                        }

                    } else {
                        System.out.println("not match 6");
                        return;
                    }
                }
            }
        }
        System.out.println("not match 7");
    }

    @Test
    public void runSpecialLogic2() {
        var code = "2365";
        var now = LocalDateTime.now();

        now = now.minusDays(15);

        System.out.println(now);
        var lastDayDataList = stockDayInfoService.getBeforeData(code, now,
                2
        );
//        日期早到晚 跌漲跌跌漲 ，需反向判斷 漲跌跌漲跌
        var stockInfoDTO = lastDayDataList.get(0);
        System.out.println(">>" + stockInfoDTO);
        var calculateSpecialTask = beanFactory.getBean(
                CalculateSpecialTask.class, stockDayInfoService, lineNotifyService, stockInfoService
        );
        System.out.println(calculateSpecialTask.matchSpecialLogic(stockInfoDTO));
    }

    @Test
    public void runPriceDeal(){
        var data = stockInfoService.getPriceDeal("2332", false);

        System.out.println(data);
    }

    @Test
    public void runTimeStamp(){
        // 定義每天的時間，這裡是下午1:30
        int hour = 13;
        int minute = 30;

        // 獲取當前日期
        LocalDateTime now = LocalDateTime.now();
        // 設定當前日期的時間為下午1:30
        LocalDateTime targetTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        // 獲取當前時區
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = targetTime.atZone(zoneId);

        // 轉換為Instant
        Instant instant = zonedDateTime.toInstant();

        // 獲取timestamp
        long timestamp = instant.toEpochMilli();

        System.out.println("Timestamp for today's 1:30 PM: " + timestamp);
    }
}
