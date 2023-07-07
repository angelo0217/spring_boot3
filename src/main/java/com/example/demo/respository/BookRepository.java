package com.example.demo.respository;
import com.example.demo.entity.db.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Add custom query methods if needed
    @Query("SELECT b, br FROM Book b JOIN BorrowingRecord br ON b.bookId = br.bookId")
    List<Object[]> findBooksWithBorrowingRecords();

    @Query(value = "SELECT b.book_id, b.title, b.author, br.* FROM books b JOIN borrowing_records br ON b.book_id = br.book_id", nativeQuery = true)
    List<Map<String, Object>> findBooksWithBorrowingRecords2();
}