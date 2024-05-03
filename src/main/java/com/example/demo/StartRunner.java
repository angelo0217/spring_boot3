package com.example.demo;

import com.example.demo.config.scheduller.ScheduledFutureManager;
import com.example.demo.service.stock.LineNotifyService;
import com.example.demo.service.stock.StockCacheService;
import com.example.demo.service.db.StockDayInfoService;
import com.example.demo.service.stock.StockInfoService;
import com.example.demo.service.db.StockNameService;
import com.example.demo.service.db.WatchStockService;
import com.example.demo.service.task.CalculateSpecialTask;
import com.example.demo.service.task.CalculateWantgooDataTask;
import com.example.demo.service.task.StockInfoAPITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Order(1)
public class StartRunner implements CommandLineRunner {

    private BeanFactory beanFactory;
    private ScheduledFutureManager scheduledFutureManager;
    private StockDayInfoService stockDayInfoService;
    private LineNotifyService lineNotifyService;
    private WatchStockService watchStockService;
    private StockCacheService stockCacheService;
    private StockInfoService stockInfoService;
    private StockNameService stockNameService;
    public StartRunner(
            BeanFactory beanFactory,
            ScheduledFutureManager scheduledFutureManager,
            StockDayInfoService stockDayInfoService,
            WatchStockService watchStockService, LineNotifyService lineNotifyService,
            StockCacheService stockCacheService,
            StockInfoService stockInfoService,
            StockNameService stockNameService
    ) {
        this.beanFactory = beanFactory;
        this.scheduledFutureManager = scheduledFutureManager;
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
        this.lineNotifyService = lineNotifyService;
        this.stockCacheService = stockCacheService;
        this.stockInfoService = stockInfoService;
        this.stockNameService = stockNameService;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("***runner*** 啟動伺服器後的第一個工作點");
        var testTask = beanFactory.getBean(StockInfoAPITask.class, stockDayInfoService, stockInfoService);
        scheduledFutureManager.addCronJob("stockTask", testTask, "0 30 17 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("stockTask",testTask, "0 27 14 * * ?");
//        var calculateTask = beanFactory.getBean(
//                CalculateStockTask.class, stockDayInfoService, watchStockService, lineNotifyService, stockCacheService,
//                stockInfoService
//        );
//        scheduledFutureManager.addCronJob("calculateTask", calculateTask, "0 0-59/15 9-13 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("calculateTask",calculateTask, "0 44 16 * * ?");

//        var calculateOpenTask =beanFactory.getBean(
//                CalculateStockOpenTask.class, stockDayInfoService, lineNotifyService, stockCacheService,
//                stockInfoService
//        );
//        scheduledFutureManager.addCronJob("calculateOpenTask",calculateOpenTask, "0 0-30/3 9 ? * MON-FRI");

//        var calculateCloseTask =beanFactory.getBean(
//                CalculateStockCloseTask.class, stockDayInfoService, lineNotifyService, stockInfoService
//        );
//        scheduledFutureManager.addCronJob("calculateCloseTask",calculateCloseTask, "30 10 19 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("calculateCloseTask",calculateCloseTask, "30 01 21 * * ?");

        var mainTrendDataTask = beanFactory.getBean(
                CalculateWantgooDataTask.class, stockDayInfoService, lineNotifyService, stockInfoService
        );
        scheduledFutureManager.addCronJob("mainTrendDataTask", mainTrendDataTask, "30 30 19 ? * MON-FRI");

        var specialTask = beanFactory.getBean(
                CalculateSpecialTask.class, stockDayInfoService, lineNotifyService, stockInfoService
        );
        scheduledFutureManager.addCronJob("specialTask", specialTask, "30 20 19 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("mainTrendDataTask",mainTrendDataTask, "30 58 21 * * ?");

//        var reasonablePrice = beanFactory.getBean(
//                CalculateReasonablePriceTask.class, stockDayInfoService, lineNotifyService, stockInfoService
//        );
//        scheduledFutureManager.addCronJob("reasonablePrice", reasonablePrice, "30 30 21 ? * FRI");

    }

}
