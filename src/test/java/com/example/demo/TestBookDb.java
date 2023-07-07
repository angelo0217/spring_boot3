package com.example.demo;

import com.example.demo.entity.dto.BookDTO;
import com.example.demo.entity.dto.BookWithBorrowingRecordDTO;
import com.example.demo.entity.dto.BorrowingRecordDTO;
import com.example.demo.service.BookService;
import com.example.demo.service.BorrowingRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBookDb {
    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowingRecordService borrowingRecordService;

    @Test
    public void insert() throws Exception {
        BookDTO bookDTO = BookDTO.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .publicationDate(LocalDate.of(1925, 4, 10))
                .isbn("9781234567890")
                .genre("Fiction")
                .price(new BigDecimal("19.99"))
                .quantity(10)
                .build();

        bookDTO = bookService.createBook(bookDTO);
        System.out.println(".............." + bookDTO.getBookId());
    }


    @Test
    public void insertRecord() throws Exception {
        var borrowingRecordDTO = BorrowingRecordDTO.builder()
                .bookId(1)
                .borrowerName("John Doe")
                .borrowDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(14))
                .status("Borrowed")
                .build();

        borrowingRecordDTO = borrowingRecordService.createBorrowingRecord(borrowingRecordDTO);
        System.out.println(".............." + borrowingRecordDTO.getRecordId());
    }

    @Test
    public void queryJoinData(){
        List<BookWithBorrowingRecordDTO> data = bookService.getBooksWithBorrowingRecords2();
        data.stream().forEach(d -> {
            System.out.println(d.getBook().getBookId());
            System.out.println(d.getBorrowingRecord().getBorrowerName());
        });
    }

    @Test
    public void queryAllBooks(){
        List<BookDTO> data = bookService.getAllBooks();
        data.stream().forEach(d -> {
            System.out.println(d.getBookId());
        });
    }
}