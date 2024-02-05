package com.example.demo.service;

import com.example.demo.entity.db.WatchStock;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.respository.WatchStockRepository;
import org.springframework.stereotype.Service;

@Service
public class WatchStockService extends BaseService<WatchStockDTO, WatchStock, Integer> {
    private final WatchStockRepository watchStockRepository;

    public WatchStockService(WatchStockRepository watchStockRepository) {
        super(watchStockRepository);
        this.watchStockRepository = watchStockRepository;
    }

    @Override
    protected WatchStock convertToEntity(WatchStockDTO watchStockDTO) {
        WatchStock watchStock = new WatchStock();
        watchStock.setStockCode(watchStockDTO.getStockCode());
        watchStock.setDetectMoney(watchStockDTO.getDetectMoney());
        watchStock.setLastDayVolumes(watchStockDTO.getLastDayVolumes());
        watchStock.setLastDateMoney(watchStockDTO.getLastDateMoney());
        watchStock.setDetectVolumes(watchStockDTO.getDetectVolumes());
        watchStock.setHappenDate(watchStockDTO.getHappenDate());
        watchStock.set_rise(watchStockDTO.is_rise());
        return watchStock;
    }

    @Override
    protected WatchStockDTO convertToDto(WatchStock watchStock) {
        var watch = WatchStockDTO.builder()
                .id(watchStock.getId())
                .stockCode(watchStock.getStockCode())
                .happenDate(watchStock.getHappenDate())
                .lastDateMoney(watchStock.getLastDateMoney())
                .detectMoney(watchStock.getDetectMoney())
                .lastDayVolumes(watchStock.getLastDayVolumes())
                .detectVolumes(watchStock.getDetectVolumes())
                .is_rise(watchStock.is_rise())
                .build();
        return watch;
    }

    @Override
    protected Integer getDtoId(WatchStockDTO watchStockDTO) {
        return watchStockDTO.getId();
    }
}
