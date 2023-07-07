package com.example.demo.entity.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class BorrowingRecordDTO {
    private int recordId;
    private int bookId;
    private String borrowerName;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;
}
