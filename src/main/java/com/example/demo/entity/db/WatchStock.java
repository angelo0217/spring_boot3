package com.example.demo.entity.db;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "watch_stock")
public class WatchStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "stockCode", nullable = false)
    private String stockCode;

    @Column(name = "happenDate", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime happenDate;

    @Column(name = "lastDateMoney", nullable = false)
    private Double lastDateMoney;

    @Column(name = "detectMoney", nullable = false)
    private Double detectMoney;

    @Column(name = "lastDayVolumes", nullable = false)
    private Integer lastDayVolumes;

    @Column(name = "detectVolumes", nullable = false)
    private Integer detectVolumes;

    @Column(name = "is_rise", nullable = false)
    private boolean is_rise;

}