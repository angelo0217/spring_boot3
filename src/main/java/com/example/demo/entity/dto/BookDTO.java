package com.example.demo.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "書籍模型")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @Schema(description = "書籍ID", example = "1")
    private int bookId;

    @Schema(description = "書籍標題", example = "The Great Gatsby")
    private String title;

    @Schema(description = "作者", example = "F. Scott Fitzgerald")
    private String author;

    @Schema(description = "出版日期", example = "2022-01-01")
    private LocalDate publicationDate;

    @Schema(description = "ISBN", example = "9781234567890")
    private String isbn;

    @Schema(description = "書籍类型", example = "小說")
    private String genre;

    @Schema(description = "價格", example = "29.99")
    private BigDecimal price;

    @Schema(description = "數量", example = "10")
    private int quantity;

}