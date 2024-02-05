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
public class StockSingleInfoDTO {
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private StockRealTimeDTO data;
}
