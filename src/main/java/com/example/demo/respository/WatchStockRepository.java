package com.example.demo.respository;

import com.example.demo.entity.db.WatchStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchStockRepository extends JpaRepository<WatchStock, Integer> {
}