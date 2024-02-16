package com.example.demo.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.dto.WatchStockDTO;

@Service
@CacheConfig(cacheNames = "stock_service")
public class StockCacheService {

    @Cacheable(key="#stockCode", cacheManager = "testManager", unless="#result == null")
    public WatchStockDTO getWatchStock(String stockCode ) {
        return null;
    }

    @CachePut(key="#stockCode", cacheManager = "testManager")
    public WatchStockDTO saveWatchStock(String stockCode, WatchStockDTO watchStockDTO) {
        return watchStockDTO;
    }

}
