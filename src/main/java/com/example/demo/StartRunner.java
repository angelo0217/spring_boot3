package com.example.demo;

import com.example.demo.config.scheduller.ScheduledFutureManager;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.WatchStockService;
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

    private WatchStockService watchStockService;
    public StartRunner(
            BeanFactory beanFactory,
            ScheduledFutureManager scheduledFutureManager,
            StockDayInfoService stockDayInfoService,
            WatchStockService watchStockService
    ) {
        this.beanFactory = beanFactory;
        this.scheduledFutureManager = scheduledFutureManager;
        this.stockDayInfoService = stockDayInfoService;
        this.watchStockService = watchStockService;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("***runner*** 啟動伺服器後的第一個工作點");
        var testTask = beanFactory.getBean(StockInfoAPITask.class, stockDayInfoService);
        scheduledFutureManager.addCronJob("stockTask",testTask, "0 30 15 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("stockTask",testTask, "0 35 18 * * ?");
        var calculateTask = beanFactory.getBean(CalculateStockTask.class, stockDayInfoService, watchStockService);
        scheduledFutureManager.addCronJob("calculateTask",calculateTask, "0 0-59/20 9-13 ? * MON-FRI");
//        scheduledFutureManager.addCronJob("calculateTask",calculateTask, "0 47 16 * * ?");
    }

}
