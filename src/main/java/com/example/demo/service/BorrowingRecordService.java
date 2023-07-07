package com.example.demo.service;

import com.example.demo.entity.db.BorrowingRecord;
import com.example.demo.entity.dto.BorrowingRecordDTO;
import com.example.demo.respository.BorrowingRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowingRecordService extends BaseService<BorrowingRecordDTO, BorrowingRecord, Integer> {
//    private final BorrowingRecordRepository borrowingRecordRepository;

    public BorrowingRecordService(BorrowingRecordRepository borrowingRecordRepository) {
        super(borrowingRecordRepository);
//        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    @Override
    protected BorrowingRecord convertToEntity(BorrowingRecordDTO borrowingRecordDTO) {
        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBookId(borrowingRecordDTO.getBookId());
        borrowingRecord.setBorrowerName(borrowingRecordDTO.getBorrowerName());
        borrowingRecord.setBorrowDate(borrowingRecordDTO.getBorrowDate());
        borrowingRecord.setReturnDate(borrowingRecordDTO.getReturnDate());
        borrowingRecord.setStatus(borrowingRecordDTO.getStatus());
        return borrowingRecord;
    }

    @Override
    protected BorrowingRecordDTO convertToDto(BorrowingRecord borrowingRecord) {
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

    @Override
    protected Integer getDtoId(BorrowingRecordDTO borrowingRecordDTO) {
        return borrowingRecordDTO.getRecordId();
    }
}

