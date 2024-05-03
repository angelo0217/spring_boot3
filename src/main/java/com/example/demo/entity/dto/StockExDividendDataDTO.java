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
public class StockExDividendDataDTO {

    @JsonProperty("year")
    private String year;

    @JsonProperty("cashDividend")
    private Double cashDividend;
}
