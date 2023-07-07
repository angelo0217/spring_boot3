package com.example.demo.service;

import com.example.demo.entity.db.BorrowingRecord;
import com.example.demo.entity.dto.BorrowingRecordDTO;
import com.example.demo.respository.BorrowingRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BorrowingRecordService {
    private final BorrowingRecordRepository borrowingRecordRepository;

    public BorrowingRecordService(BorrowingRecordRepository borrowingRecordRepository) {
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    public BorrowingRecordDTO createBorrowingRecord(BorrowingRecordDTO borrowingRecordDTO) {
        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBookId(borrowingRecordDTO.getBookId());
        borrowingRecord.setBorrowerName(borrowingRecordDTO.getBorrowerName());
        borrowingRecord.setBorrowDate(borrowingRecordDTO.getBorrowDate());
        borrowingRecord.setReturnDate(borrowingRecordDTO.getReturnDate());
        borrowingRecord.setStatus(borrowingRecordDTO.getStatus());

        BorrowingRecord savedRecord = borrowingRecordRepository.save(borrowingRecord);

        // 將保存的借閱記錄實體轉換為 DTO 並返回
        return BorrowingRecordDTO.mapBorrowingRecordToDTO(savedRecord);
    }

    public BorrowingRecordDTO getBorrowingRecordById(int recordId) {
        Optional<BorrowingRecord> optionalRecord = borrowingRecordRepository.findById(recordId);
        if (optionalRecord.isPresent()) {
            BorrowingRecord borrowingRecord = optionalRecord.get();
            return BorrowingRecordDTO.mapBorrowingRecordToDTO(borrowingRecord);
        }
        return null; // 找不到借閱記錄
    }

    public List<BorrowingRecordDTO> getAllBorrowingRecords() {
        List<BorrowingRecord> borrowingRecords = borrowingRecordRepository.findAll();
        return borrowingRecords.stream()
                .map(BorrowingRecordDTO::mapBorrowingRecordToDTO)
                .collect(Collectors.toList());
    }

    public BorrowingRecordDTO updateBorrowingRecord(BorrowingRecordDTO borrowingRecordDTO) {
        Optional<BorrowingRecord> optionalRecord = borrowingRecordRepository.findById(borrowingRecordDTO.getRecordId());
        if (optionalRecord.isPresent()) {
            BorrowingRecord borrowingRecord = optionalRecord.get();
            borrowingRecord.setBookId(borrowingRecordDTO.getBookId());
            borrowingRecord.setBorrowerName(borrowingRecordDTO.getBorrowerName());
            borrowingRecord.setBorrowDate(borrowingRecordDTO.getBorrowDate());
            borrowingRecord.setReturnDate(borrowingRecordDTO.getReturnDate());
            borrowingRecord.setStatus(borrowingRecordDTO.getStatus());

            BorrowingRecord updatedRecord = borrowingRecordRepository.save(borrowingRecord);

            // 將更新的借閱記錄實體轉換為 DTO 並返回
            return BorrowingRecordDTO.mapBorrowingRecordToDTO(updatedRecord);
        }
        return null; // 找不到借閱記錄
    }

    public boolean deleteBorrowingRecord(int recordId) {
        Optional<BorrowingRecord> optionalRecord = borrowingRecordRepository.findById(recordId);
        if (optionalRecord.isPresent()) {
            BorrowingRecord borrowingRecord = optionalRecord.get();
            borrowingRecordRepository.delete(borrowingRecord);
            return true;
        }
        return false; // 找不到借閱記錄
    }
}

