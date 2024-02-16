package com.example.demo.respository;

import com.example.demo.entity.db.StockDayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockDayInfoRepository extends JpaRepository<StockDayInfo, Integer> {
    @Query("SELECT COUNT(e.id) FROM StockDayInfo e WHERE DATE_FORMAT(e.dataDate, '%Y-%m-%d') = :dataDate")
    long countByDataDate(String dataDate);

    List<StockDayInfo> getAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(int greater, int less, Long tradeDate);

    @Query("SELECT MAX(e.tradeDate) FROM StockDayInfo e")
    Long findMaxTraceDate();

    @Query("SELECT MAX(e.dataDate) FROM StockDayInfo e")
    LocalDateTime findMaxDataDate();


    @Query("SELECT s FROM StockDayInfo s WHERE s.close > :greater AND s.close < :less AND DATE_FORMAT(s.dataDate, '%Y-%m-%d') = :dataDate ORDER BY s.dataDate DESC")
    List<StockDayInfo> findAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(@Param("greater") int greater, @Param("less") int less, @Param("dataDate") String dataDate);

    @Query("SELECT s FROM StockDayInfo s WHERE s.stockCode = :stockCode AND DATE_FORMAT(s.dataDate, '%Y-%m-%d') < :dataDate ORDER BY s.dataDate DESC limit 3")
    List<StockDayInfo> findBeforeData(@Param("stockCode") String stockCode, @Param("dataDate") String dataDate);

}