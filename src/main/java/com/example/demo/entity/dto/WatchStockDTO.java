package com.example.demo.entity.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchStockDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("stockCode")
    private String stockCode;

    @JsonProperty("happenDate")
    private LocalDateTime happenDate;

    @JsonProperty("lastDateMoney")
    private Double lastDateMoney;

    @JsonProperty("detectMoney")
    private Double detectMoney;

    @JsonProperty("lastDayVolumes")
    private Integer lastDayVolumes;

    @JsonProperty("detectVolumes")
    private Integer detectVolumes;

    @JsonProperty("is_rise")
    private boolean is_rise;
}
