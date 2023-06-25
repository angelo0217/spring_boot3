package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
// no args is for stream model
@NoArgsConstructor
public class User implements Serializable {
    private String name;
    private int age;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date create_date;
    private BigDecimal number;
    private long long_num;

//    @JsonCreator
//    public User(@JsonProperty("name")String name,
//                @JsonProperty("age")int age,
//                @JsonProperty("create_date")Date create_date,
//                @JsonProperty("number")BigDecimal number,
//                @JsonProperty("long_num")long long_num) {
//        this.name = name;
//        this.age = age;
//        this.create_date =create_date;
//        this.number = number;
//        this.long_num = long_num;
//    }
}
