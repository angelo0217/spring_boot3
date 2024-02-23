package com.example.demo.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
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

    public Double getRealTimePrice(){
        return this.getData().getO().get(0);
    }

    public Integer getRealTimeVolume() {
        return this.getData().getV().stream().filter(Objects::nonNull)
                   .mapToInt(Double::intValue)
                   .sum();
    }
}
