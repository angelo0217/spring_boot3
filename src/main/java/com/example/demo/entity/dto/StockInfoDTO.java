package com.example.demo.entity.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockInfoDTO {
    private Integer dbId = Integer.MIN_VALUE;

    @JsonProperty("id")
    private String stockCode;

    @JsonProperty("tradeDate")
    private Long tradeDate;

    @JsonProperty("time")
    private Long time;

    @JsonProperty("flat")
    private Double flat;

    @JsonProperty("floor")
    private Double floor;

    @JsonProperty("ceil")
    private Double ceil;

    @JsonProperty("open")
    private Double open;

    @JsonProperty("high")
    private Double high;

    @JsonProperty("low")
    private Double low;

    @JsonProperty("close")
    private Double close;

    @JsonProperty("volume")
    private Integer volume;

    @JsonProperty("millionAmount")
    private Double millionAmount;

    @JsonProperty("previousClose")
    private Double previousClose;

    @JsonProperty("previousVolume")
    private Integer previousVolume;

    @JsonProperty("previousMillionAmount")
    private Double previousMillionAmount;
}
