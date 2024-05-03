package com.example.demo.service.stock;

import com.example.demo.entity.dto.WatchStockDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "stock_service")
public class StockCacheService {

    @Cacheable(key = "#stockCode", cacheManager = "testManager", unless = "#result == null")
    public WatchStockDTO getWatchStock(String stockCode) {
        return null;
    }

    @CachePut(key = "#stockCode", cacheManager = "testManager", unless = "#result == null")
    public WatchStockDTO saveWatchStock(String stockCode, WatchStockDTO watchStockDTO) {
        return watchStockDTO;
    }

    @Cacheable(value = "special_stock", key = "#stockCode", cacheManager = "testManager", unless = "#result == null")
    public WatchStockDTO getSpecialWatchStock(String stockCode) {
        return null;
    }

    @CachePut(value = "special_stock", key = "#stockCode", cacheManager = "testManager", unless = "#result == null")
    public WatchStockDTO saveSpecialWatchStock(String stockCode, WatchStockDTO watchStockDTO) {
        return watchStockDTO;
    }

}
