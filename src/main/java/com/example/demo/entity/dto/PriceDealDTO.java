package com.example.demo.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PriceDealDTO {
    @JsonProperty("dealOnBidPrice")
    private Integer dealOnBidPrice;

    @JsonProperty("dealOnAskPrice")
    private Integer dealOnAskPrice;
}
