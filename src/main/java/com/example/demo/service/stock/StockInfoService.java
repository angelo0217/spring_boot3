package com.example.demo.service.stock;

import static com.example.demo.constant.StockConst.GECKO_DRIVER;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.PriceDealDTO;
import com.example.demo.entity.dto.StockExDividendDataDTO;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockMainTrendDataDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.StockWantgoo;
import com.example.demo.utils.JsonUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class StockInfoService {
    private String[] etfCode = {
            "0050",
            "0051",
            "0052",
            "0053",
            "0055",
            "0056",
            "0057",
            "0061",
            "006203",
            "006205",
            "006204",
            "006206",
            "006207",
            "006208",
            "00631L",
            "00632R",
            "00633L",
            "00634R",
            "00636",
            "00635U",
            "00637L",
            "00638R",
            "00639",
            "00642U",
            "00640L",
            "00641R",
            "00645",
            "00643",
            "00646",
            "00647L",
            "00648R",
            "00650L",
            "00651R",
            "00655L",
            "00656R",
            "00652",
            "00653L",
            "00654R",
            "00657",
            "00660",
            "00661",
            "00662",
            "00663L",
            "00664R",
            "00665L",
            "00666R",
            "00675L",
            "00676R",
            "00673R",
            "00674R",
            "00669R",
            "00668",
            "00678",
            "00680L",
            "00681R",
            "00670L",
            "00671R",
            "00682U",
            "00683L",
            "00684R",
            "00685L",
            "00686R",
            "00690",
            "00688L",
            "00689R",
            "00693U",
            "00692",
            "00700",
            "00703",
            "00709",
            "00701",
            "00702",
            "00710B",
            "00711B",
            "00712",
            "00706L",
            "00707R",
            "00708L",
            "00713",
            "00714",
            "00715L",
            "00717",
            "00730",
            "00728",
            "00731",
            "00733",
            "00738U",
            "00735",
            "00736",
            "00737",
            "00739",
            "00752",
            "00753L",
            "00757",
            "00763U",
            "00762",
            "00770",
            "00775B",
            "00783",
            "00830",
            "00771",
            "00851",
            "00852L",
            "00850",
            "00861",
            "00865B",
            "00875",
            "00876",
            "00878",
            "00881",
            "00882",
            "00885",
            "00891",
            "00892",
            "00893",
            "00895",
            "00894",
            "00896",
            "00897",
            "00898",
            "00901",
            "00900",
            "00899",
            "00903",
            "00902",
            "00904",
            "00905",
            "00908",
            "00907",
            "00911",
            "00912",
            "00909",
            "00910",
            "00913",
            "00915",
            "00917",
            "00916",
            "00919",
            "00920",
            "00918",
            "00921",
            "00923",
            "00922",
            "00925",
            "00924",
            "00926",
            "00927",
            "00929",
            "00930",
            "00932",
            "00934",
            "00935",
            "00936",
            "00941",
            "00939",
            "00940",
    };

    public boolean containsETFCode(String code) {
        List<String> etfCodeList = Arrays.asList(etfCode);
        return etfCodeList.contains(code);
    }

    public StockSingleInfoDTO getStockInfo(String stockCode) {
        var restTemplate = new RestTemplate();
        String apiUrl =
                "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:" + stockCode
                        + ":STOCK&quote=1";

        ResponseEntity<StockSingleInfoDTO> responseEntity = restTemplate.exchange(apiUrl,
                HttpMethod.GET, null,
                StockSingleInfoDTO.class
        );
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
            var info = responseEntity.getBody();
            if (info.getData().getO() != null && !info.getData().getO().isEmpty()) {
                return info;
            } else {
                return null;
            }
        }
        log.error("failed to call api {}", responseEntity);
        throw new RuntimeException("no data");
    }

    public String getMonthDayStr(int val) {
        if (val < 10)
            return "0" + val;
        return ""+val;
    }


    public List<StockInfoDTO> getStockInfoWithGoogleDriver() {
//        https://googlechromelabs.github.io/chrome-for-testing/
        System.setProperty("webdriver.chrome.driver", StockConst.CHROME_DRIVER);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setBinary("D:\\chrome-win64\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);
//        System.setProperty("webdriver.gecko.driver", GECKO_DRIVER);
//        WebDriver driver = new FirefoxDriver();


        try {
            // 打開目標網頁
            driver.get("https://www.wantgoo.com/investrue/all-quote-info");
            String pageSource = driver.getPageSource();
            String regex = "<(.*?)>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageSource);
            String result = matcher.replaceAll("");
            StockInfoDTO[] stockInfoDTOS = JsonUtil.jsonToObject(result, StockInfoDTO[].class);
            System.out.println(stockInfoDTOS);
            return Arrays.asList(stockInfoDTOS);
        } finally {
            // 關閉瀏覽器
            driver.quit();
        }
    }

    public PriceDealDTO getPriceDeal(String stockCode, boolean isOutPrice) {
        System.setProperty("webdriver.chrome.driver", StockConst.CHROME_DRIVER);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setBinary("D:\\chrome-win64\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);
        var url = "https://www.wantgoo.com/investrue/"+stockCode+"/topfivepieces";
        if (isOutPrice)
            url = "https://www.wantgoo.com/investrue/"+stockCode+"/historical-topfivepieces?v="+ this.getTimeStamp();
        try {
            // 打開目標網頁
            driver.get("https://www.wantgoo.com/stock/"+stockCode);
            // 等待一段時間，確保網頁完全載入
            try {
//                Thread.sleep(5000); // 等待10秒

                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                Object priceDeal = jsExecutor.executeScript(
                        "return fetch('"+url+"')" +
                                ".then(response => response.json())" +
                                ".then(data => data);");

                var priceDealJson = JsonUtil.objectToJson(priceDeal);


                return JsonUtil.jsonToObject(priceDealJson, PriceDealDTO.class);
            } catch (Exception e) {
            }
            return null;
        } finally {
            // 關閉瀏覽器
            driver.quit();
        }
    }

    public long getTimeStamp(){
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

        return timestamp;
    }

    public StockWantgoo<StockExDividendDataDTO[], StockMainTrendDataDTO[]> getWantgooData(String stockCode) {
        String mainUrl = "https://www.wantgoo.com/stock/"+stockCode;
        if (containsETFCode(stockCode))
            mainUrl = "https://www.wantgoo.com/stock/etf/"+stockCode;

        String mainTrend = "https://www.wantgoo.com/stock/"+stockCode+"/major-investors/main-trend-data";
        String exDividend = "https://www.wantgoo.com/stock/" +stockCode+ "/dividend-policy/ex-dividend-data";
        return this.getApiRes(StockExDividendDataDTO[].class, StockMainTrendDataDTO[].class, stockCode, mainUrl, exDividend, mainTrend);
    }

    public <T, K> StockWantgoo<T, K> getApiRes( Class<T> reasonablePriceClz, Class<K> mainTrendDataClz,
            String stockCode,
            String mainUrl,
            String reasonablePriceUlr,
            String mainTrendDataUrl){
        System.setProperty("webdriver.chrome.driver", StockConst.CHROME_DRIVER);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setBinary("D:\\chrome-win64\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);
        // 使用JavascriptExecutor在瀏覽器中執行API請求，並取得結果
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        try{
            var result = new StockWantgoo<T, K>();
            // 打開網頁
            driver.get(mainUrl + "/major-investors/main-trend");

            // 等待一段時間，確保網頁完全載入
            try {
                Thread.sleep(5000); // 等待10秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Object mainTrendResult = jsExecutor.executeScript(
                    "return fetch('"+mainTrendDataUrl+"')" +
                            ".then(response => response.json())" +
                            ".then(data => data);");

            var mainTrendResultJson = JsonUtil.objectToJson(mainTrendResult);

            result.setMainTrendData(JsonUtil.jsonToObject(mainTrendResultJson, mainTrendDataClz));

            driver.get(mainUrl + "/dividend-policy/ex-dividend");
            try {
                Thread.sleep(2500); // 等待10秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Object apiResult = jsExecutor.executeScript(
                    "return fetch('"+reasonablePriceUlr+"')" +
                            ".then(response => response.json())" +
                            ".then(data => data);");

            var apiResultJson = JsonUtil.objectToJson(apiResult);

            result.setReasonablePriceData(JsonUtil.jsonToObject(apiResultJson, reasonablePriceClz));

            if (!containsETFCode(stockCode)) {
                Object pbr = jsExecutor.executeScript(
                        "return fetch('https://www.wantgoo.com/stock/" + stockCode + "/pbr')" +
                                ".then(response => response.json())" +
                                ".then(data => data);");
                double pbrValue = ((Number) ((Map<?, ?>) pbr).get("pbr") != null)
                        ? ((Number) ((Map<?, ?>) pbr).get("pbr")).doubleValue()
                        : 0.0; // 如果为null，则设置默认值为0.0

                result.setPbr(pbrValue);
                Object per = jsExecutor.executeScript(
                        "return fetch('https://www.wantgoo.com/stock/" + stockCode + "/per')" +
                                ".then(response => response.json())" +
                                ".then(data => data);");

                double perValue = ((Number) ((Map<?, ?>) per).get("per") != null)
                        ? ((Number) ((Map<?, ?>) pbr).get("per")).doubleValue()
                        : 0.0; // 如果为null，则设置默认值为0.0
                result.setPer(perValue);
            }
            return result;
        } finally {
            // 關閉瀏覽器
            driver.quit();
        }
    }
}
