package com.example.demo.controller;

import com.example.demo.config.scheduller.ScheduledFutureManager;
import com.example.demo.service.task.TestTask;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduler")
public class SchedulerCtrl {
    private BeanFactory beanFactory;

    private ScheduledFutureManager scheduledFutureManager;
    public SchedulerCtrl(BeanFactory beanFactory, ScheduledFutureManager scheduledFutureManager) {
        this.beanFactory = beanFactory;
        this.scheduledFutureManager = scheduledFutureManager;
    }

    @Operation(summary = "cron job register")
    @GetMapping("/cron_job/register")
    public String addCronJob(@RequestParam(name="cron", defaultValue = "0 0/1 * * * ?") String cronExpression){
        var testTask = beanFactory.getBean(TestTask.class, "cron_job name");
        scheduledFutureManager.addCronJob("key_cron",testTask, cronExpression);
        return "hello";
    }

    @GetMapping("/feature/{delay}/register")
    public String addOnceFeatureJob(@PathVariable int delay){
        var testTask = beanFactory.getBean(TestTask.class, "feature name");
        scheduledFutureManager.addTestFuture("key_feature", testTask, delay);
        return "hello";
    }

    @GetMapping("/scheduled_fix/{initialDelay}/{period}/register")
    public String addFixJob(@PathVariable int initialDelay, @PathVariable int period){
        var testTask = beanFactory.getBean(TestTask.class, "scheduled_fix name");
        scheduledFutureManager.addTestFix("key_fix_job", testTask, initialDelay, period);
        return "hello";
    }


    @DeleteMapping("/job/cancel")
    public String cancelJob(){
        scheduledFutureManager.remove("key_cron");
        return "hello";
    }
}