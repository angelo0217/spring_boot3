package com.example.demo.respository;

import com.example.demo.entity.db.StockDayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDayInfoRepository extends JpaRepository<StockDayInfo, Integer> {
    long countByTradeDate(Long traceDate);
    List<StockDayInfo> getAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(int greater, int less, Long tradeDate);
    @Query("SELECT MAX(e.tradeDate) FROM StockDayInfo e")
    Long findMaxTraceDate();
}