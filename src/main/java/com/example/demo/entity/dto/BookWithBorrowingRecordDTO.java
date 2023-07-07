package com.example.demo.entity.dto;


import lombok.Data;

@Data
public class BookWithBorrowingRecordDTO {
    private BookDTO book;
    private BorrowingRecordDTO borrowingRecord;
}
