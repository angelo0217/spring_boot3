package com.example.demo.respository;

import com.example.demo.entity.db.StockName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockNameRepository extends JpaRepository<StockName, Integer> {
}