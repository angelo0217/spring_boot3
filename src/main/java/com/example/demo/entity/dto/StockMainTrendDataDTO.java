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
public class StockMainTrendDataDTO {

    @JsonProperty("date")
    private Long date;

    @JsonProperty("close")
    private Double close;

    @JsonProperty("stockAgentDiff")
    private int stockAgentDiff;

    @JsonProperty("stockAgentMainPower")
    private int stockAgentMainPower;

    @JsonProperty("skp5")
    private Double skp5;

    @JsonProperty("skp20")
    private Double skp20;
}
