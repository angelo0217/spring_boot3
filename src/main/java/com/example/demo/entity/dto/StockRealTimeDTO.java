package com.example.demo.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRealTimeDTO {
    @JsonProperty("s")
    private String s;
    @JsonProperty("t")
    private List<Long> t;
    @JsonProperty("o")
    private List<Double> o;
    @JsonProperty("h")
    private List<Double> h;
    @JsonProperty("1")
    private List<Double> l;
    @JsonProperty("c")
    private List<Double> c;
    @JsonProperty("v")
    private List<Double> v;
    @JsonProperty("session")
    private List<List<Long>> session;
    @JsonProperty("nextTime")
    private Long nextTime;
}
