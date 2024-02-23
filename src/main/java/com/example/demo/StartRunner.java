package com.example.demo;

import com.example.demo.config.scheduller.ScheduledFutureManager;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.StockCacheService;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.StockSinglyService;
import com.example.demo.service.WatchStockService;
import com.example.demo.service.task.CalculateStockOpenTask;
import com.example.demo.service.task.CalculateStockCloseTask;
import com.example.demo.service.task.CalculateStockTask;
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
    private StockSinglyService stockSinglyService;

    public StartRunner(
            BeanFactory beanFactory,
            ScheduledFutureManager scheduledFutureManager,
            StockDayInfoService stockDayInfoService,
            WatchStockService watchStockService, LineNotifyService lineNotifyService,
            StockCacheService stockCacheService,
            StockSinglyService stockSinglyService
    ) {
        this.beanFactory = beanFactory;
        this.scheduledFutureManager = scheduledFutureManager;
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
        this.lineNotifyService = lineNotifyService;
        this.stockCacheService = stockCacheService;
        this.stockSinglyService = stockSinglyService;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("***runner*** 啟動伺服器後的第一個工作點");
        var testTask = beanFactory.getBean(StockInfoAPITask.class, stockDayInfoService);
        scheduledFutureManager.addCronJob("stockTask", testTask, "0 30 17 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("stockTask",testTask, "0 35 18 * * ?");
        var calculateTask = beanFactory.getBean(
                CalculateStockTask.class, stockDayInfoService, watchStockService, lineNotifyService, stockCacheService,
                stockSinglyService
        );
        scheduledFutureManager.addCronJob("calculateTask", calculateTask, "0 0-59/15 9-13 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("calculateTask",calculateTask, "0 44 16 * * ?");

        var calculateOpenTask =beanFactory.getBean(
                CalculateStockOpenTask.class, stockDayInfoService, lineNotifyService, stockCacheService,
                stockSinglyService
        );
        scheduledFutureManager.addCronJob("calculateOpenTask",calculateOpenTask, "0 0-30/2 9 ? * MON-FRI");

        var calculateCloseTask =beanFactory.getBean(
                CalculateStockCloseTask.class, stockDayInfoService, lineNotifyService, stockSinglyService
        );
        scheduledFutureManager.addCronJob("calculateCloseTask",calculateCloseTask, "30 34 19 ? * MON-FRI");

    }

}
