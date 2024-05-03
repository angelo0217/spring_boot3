package com.example.demo.entity.db;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_data")
public class StockDayInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String stockCode;
    private Long tradeDate;
    private Long time;
    private Double flat;
    private Double floor;
    private Double ceil;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Integer volume;
    private Double millionAmount;
    private Double previousClose;
    private Integer previousVolume;
    private Double previousMillionAmount;
    private String stockName;
    @Column(name = "dataDate", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataDate;
}
