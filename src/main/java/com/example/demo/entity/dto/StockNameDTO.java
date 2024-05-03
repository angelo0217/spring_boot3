package com.example.demo.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockNameDTO {
    private Integer id;
    private String stockCode;
    private String stockName;
}
