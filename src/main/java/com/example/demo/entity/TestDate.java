package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDate {
    /**
     * 以下是一些常见的 format 值的示例：
     *
     * format = "date"：表示日期格式，例如 "yyyy-MM-dd"。
     * format = "date-time"：表示日期时间格式，例如 "yyyy-MM-dd'T'HH:mm:ss.SSSZ"。
     * format = "time"：表示时间格式，例如 "HH:mm:ss"。
     * format = "email"：表示电子邮件格式。
     * format = "uri"：表示 URI 或 URL 格式。
     * format = "uuid"：表示 UUID 格式。
     * format = "password"：表示密码格式。
     **/
    @Schema(description = "日期", example = "2023-07-12 11:00:00", type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime testTime;
}
