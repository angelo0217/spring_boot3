package com.example.demo.constant;

import com.example.demo.entity.dto.StockInfoDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockConst {

    public static final Integer B_MIN_CLOSE = 11;// 計算使用，取前一天收盤價
    public static final Integer B_MAX_CLOSE = 60;
    public static final Double MAGNIFICATION = 1.8; // 交易量大於前一日幾倍要通知
    public static final Integer DEFAULT_VOLUMES = 1000;
    public static final Double MIN_CLOSE = 10.0; // 拉取每日的股票所有資訊，收盤價錢，寫入db使用
    public static final Double MAX_CLOSE = 40.0;
    public static final Double RISE_LINE = 3.0;
    public static final Integer RISE_REF_DATE = 2; // 往前抓幾日，判斷是否漲幅超過n %
    public static final String NOTHING = "nothing";
    public static final String CHROME_DRIVER =  "src\\main\\resources\\driver\\chromedriver.exe";
    public static final String GECKO_DRIVER =  "src\\main\\resources\\driver\\geckodriver.exe";

    @Getter
    public enum REASON {
        NOTHING("nothing"),
        MAGNIFICATION_UP("交易量倍率達到 %s 倍, 金額漲 %.2f 百分比"),
        MAGNIFICATION_DOWN("出現賣壓，交易量倍率達到 %s 倍,  金額超過昨日最高, 交易金額跌 %.2f 百分比"),
        OVER_MA5("前2個交易日未超過周線，今日超過周線"),
        TODAY_START_OVER_MA5("前3日小於5日20日線，今日超過5日線，且漲幅已過1%，可能即將漲"),
        OVER_LAST_DAY_HIGH("昨日漲，今日超過昨日最高點，可能突破壓力"),
        RISE("漲幅超過" + StockConst.RISE_LINE + "百分比");

        private String description;

        REASON(String description) {
            this.description = description;
        }

        public static REASON getStockReason(
                Integer detectVolumes,
                Double detectMoney,
                StockInfoDTO stockInfo
        ) {
            if (StockConst.MAGNIFICATION < (detectVolumes / stockInfo.getVolume())) {
                if (detectMoney > stockInfo.getHigh()) {
                    return REASON.MAGNIFICATION_UP;
                }
//                else {
//                    return REASON.MAGNIFICATION_DOWN;
//                }
            } else if (((detectMoney - stockInfo.getClose()) / stockInfo.getClose()) * 100 > StockConst.RISE_LINE
                    && detectVolumes > StockConst.DEFAULT_VOLUMES) {
                return REASON.RISE;
            }
            return REASON.NOTHING;
        }
    }
}
