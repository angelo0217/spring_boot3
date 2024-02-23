package com.example.demo.constant;

import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.utils.StockUtils;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockConst {

    public static final Integer B_MIN_CLOSE = 12; // 計算使用，取前一天收盤價
    public static final Integer B_MAX_CLOSE = 27;
    public static final Double MAGNIFICATION = 1.8; // 交易量大於前一日幾倍要通知
    public static final Integer DEFAULT_VOLUMES = 1500;
    public static final Double MIN_CLOSE = 10.0; // 拉取每日的股票所有資訊，收盤價錢，寫入db使用
    public static final Double MAX_CLOSE = 35.0;
    public static final Double RISE_LINE = 2.0;
    public static final Integer RISE_REF_DATE = 2; // 往前抓幾日，判斷是否漲幅超過n %
    public static final String NOTHING = "nothing";


    @Getter
    public enum REASON {
        NOTHING("nothing"),
        MAGNIFICATION_UP("交易量倍率達到 %s 倍, 金額漲 %.2f 百分比"),
        MAGNIFICATION_DOWN("出現賣壓，交易量倍率達到 %s 倍, 交易金額跌 %.2f 百分比"),
        RISE("漲幅超過" + StockConst.RISE_LINE + "百分比");

        private String description;

        REASON(String description) {
            this.description = description;
        }

        public static REASON getStockReason(
                Integer detectVolumes, Integer lastVolumes,
                Double detectMoney, Double lastMoney
        ) {

            if (StockConst.MAGNIFICATION < (detectVolumes / lastVolumes)) {
                if (detectMoney > lastMoney) {
                    return REASON.MAGNIFICATION_UP;
                } else {
                    return REASON.MAGNIFICATION_DOWN;
                }
            } else if (((detectMoney - lastMoney) / lastMoney) * 100 > StockConst.RISE_LINE
                    && detectVolumes > StockConst.DEFAULT_VOLUMES) {
                return REASON.RISE;
            }
            return REASON.NOTHING;
        }
    }
}
