package com.example.demo.entity.dto;


import com.example.demo.entity.db.BorrowingRecord;
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

    public static BorrowingRecordDTO mapBorrowingRecordToDTO(BorrowingRecord borrowingRecord) {
        BorrowingRecordDTO borrowingRecordDTO = BorrowingRecordDTO.builder()
                .recordId(borrowingRecord.getRecordId())
                .bookId(borrowingRecord.getBookId())
                .borrowerName(borrowingRecord.getBorrowerName())
                .borrowDate(borrowingRecord.getBorrowDate())
                .returnDate(borrowingRecord.getReturnDate())
                .status(borrowingRecord.getStatus())
                .build();
        return borrowingRecordDTO;
    }
}
