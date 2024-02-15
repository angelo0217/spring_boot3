package com.example.demo.constant;

public class StockConst {
    public static final Integer B_MIN_CLOSE = 12; //計算使用，取前一天收盤價
    public static final Integer B_MAX_CLOSE = 27;
    public static final Double MAGNIFICATION = 1.8; //交易量大於前一日幾倍要通知
    public static final Integer DEFAULT_VOLUMES = 1000;
    public static final Double MIN_CLOSE = 10.0; //拉取每日的股票所有資訊，收盤價錢，寫入db使用
    public static final Double MAX_CLOSE = 35.0;
}
