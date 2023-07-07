package com.example.demo.respository;

import com.example.demo.entity.db.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {
    // Add custom query methods if needed
}

