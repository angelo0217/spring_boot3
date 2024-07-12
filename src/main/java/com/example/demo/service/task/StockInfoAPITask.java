package com.example.demo.service.task;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.utils.StockUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("StockInfoAPITask")
@Slf4j
public class StockInfoAPITask implements Runnable {

    private StockDayInfoService stockDayInfoService;
    private StockInfoService stockInfoService;


    public StockInfoAPITask(StockDayInfoService stockDayInfoService, StockInfoService stockInfoService) {
        this.stockDayInfoService = stockDayInfoService;
        this.stockInfoService = stockInfoService;
    }



    @Override
    public void run() {

        var tradeDate = stockDayInfoService.getMaxTraceDate();
        log.info("==============================start stock task");
        List<StockInfoDTO> stockInfoDTOS = this.stockInfoService.getStockInfoWithGoogleDriver();
        var newList = stockInfoDTOS.stream()
                                   .filter(v -> StockUtils.isEStock(v.getStockCode()))
//                                   .filter(e -> e.getClose() > StockConst.MIN_CLOSE
//                                           && e.getClose() <= StockConst.MAX_CLOSE)
                                   .collect(Collectors.toList());
        if (tradeDate != stockInfoDTOS.get(0).getTradeDate()) {
            this.stockDayInfoService.saveAll( newList);
        } else {
            log.info("----------- data exist");
        }

    }

}
