package com.example.demo.respository;

import com.example.demo.entity.db.StockDayInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDayInfoRepository extends JpaRepository<StockDayInfo, Integer> {
    @Query("SELECT COUNT(e.id) FROM StockDayInfo e WHERE DATE_FORMAT(e.dataDate, '%Y-%m-%d') = :dataDate")
    long countByDataDate(String dataDate);

    List<StockDayInfo> getAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(
            int greater, int less, Long tradeDate
    );

    @Query("SELECT MAX(e.tradeDate) FROM StockDayInfo e")
    Long findMaxTraceDate();

    @Query("SELECT MAX(e.dataDate) FROM StockDayInfo e")
    LocalDateTime findMaxDataDate();


    @Query(value = "SELECT s.id, s.stockCode, s.tradeDate, s.time, s.flat, s.floor, s.ceil, s.open, s.high, s.low, s"
            + ".close, s.volume, s.millionAmount, s.previousClose, s.previousVolume, s.previousMillionAmount, sn.stockName as stockName, s.dataDate "
            + " FROM stock_data s LEFT JOIN stock_name sn ON s.stockCode = sn.stockCode"
            + " WHERE s.close > :less AND s.close < :greater AND DATE_FORMAT(s.dataDate, '%Y-%m-%d') = :dataDate ORDER BY s.dataDate DESC",
            nativeQuery = true)
    List<StockDayInfo> findAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(
            @Param("less") int less, @Param("greater") int greater, @Param("dataDate") String dataDate
    );

    @Query(value =
            "SELECT s.id, s.stockCode, s.tradeDate, s.time, s.flat, s.floor, s.ceil, s.open, s.high, s.low, s.close, s.volume, s.millionAmount, s.previousClose, s.previousVolume, s.previousMillionAmount, sn.stockName as stockName, s.dataDate "
                    + "FROM stock_data s LEFT JOIN stock_name sn ON s.stockCode = sn.stockCode "
                    + "WHERE s.stockCode = :stockCode AND DATE_FORMAT(s.dataDate, '%Y-%m-%d') < :dataDate ORDER BY s.dataDate DESC LIMIT :limit",
            nativeQuery = true)
    List<StockDayInfo> findBeforeData(
            @Param("stockCode") String stockCode, @Param("dataDate") String dataDate,
            @Param("limit") int limit
    );

//    @Query(value ="select a.stockCode from ("
//            + "select stockCode, count(id) as cnt from stock_data sd  group by stockCode \n"
//            + ")as a where a.cnt = 1",  nativeQuery = true)
    @Query(value ="select stockCode from stock_data group by stockCode ",  nativeQuery = true)
    List<String> findStocksWithOneRecord();

}