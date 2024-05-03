package com.example.demo.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockWantgoo<T, K> {
    private double per;
    private double pbr;
    private T reasonablePriceData;
    private K mainTrendData;
}
