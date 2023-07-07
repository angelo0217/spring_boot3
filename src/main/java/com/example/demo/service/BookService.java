package com.example.demo.service;

import com.example.demo.entity.db.Book;
import com.example.demo.entity.db.BorrowingRecord;
import com.example.demo.entity.dto.BookDTO;
import com.example.demo.entity.dto.BookWithBorrowingRecordDTO;
import com.example.demo.entity.dto.BorrowingRecordDTO;
import com.example.demo.respository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setIsbn(bookDTO.getIsbn());
        book.setGenre(bookDTO.getGenre());
        book.setPrice(bookDTO.getPrice());
        book.setQuantity(bookDTO.getQuantity());
        Book savedBook = bookRepository.save(book);

        // Map the saved book entity back to DTO and return
        return BookDTO.mapBookToDTO(savedBook);
    }

    public BookDTO getBookById(int bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            return BookDTO.mapBookToDTO(book);
        }
        return null; // Book not found
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(BookDTO::mapBookToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO updateBook(BookDTO bookDTO) {
        Optional<Book> optionalBook = bookRepository.findById(bookDTO.getBookId());
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            book.setPublicationDate(bookDTO.getPublicationDate());
            book.setIsbn(bookDTO.getIsbn());
            book.setGenre(bookDTO.getGenre());
            book.setPrice(bookDTO.getPrice());
            book.setQuantity(bookDTO.getQuantity());

            Book updatedBook = bookRepository.save(book);

            // Map the updated book entity back to DTO and return
            return BookDTO.mapBookToDTO(updatedBook);
        }
        return null; // Book not found
    }

    public boolean deleteBook(int bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            bookRepository.delete(book);
            return true;
        }
        return false; // Book not found
    }

    public List<BookWithBorrowingRecordDTO> getBooksWithBorrowingRecords() {
        List<Object[]> results = bookRepository.findBooksWithBorrowingRecords();
        List<BookWithBorrowingRecordDTO> bookWithBorrowingRecords = new ArrayList<>();

        for (Object[] result : results) {
            Book book = (Book) result[0];
            BorrowingRecord borrowingRecord = (BorrowingRecord) result[1];

            BookDTO bookDTO = BookDTO.mapBookToDTO(book);
            BorrowingRecordDTO borrowingRecordDTO = BorrowingRecordDTO.mapBorrowingRecordToDTO(borrowingRecord);

            BookWithBorrowingRecordDTO bookWithBorrowingRecordDTO = new BookWithBorrowingRecordDTO();
            bookWithBorrowingRecordDTO.setBook(bookDTO);
            bookWithBorrowingRecordDTO.setBorrowingRecord(borrowingRecordDTO);

            bookWithBorrowingRecords.add(bookWithBorrowingRecordDTO);
        }

        return bookWithBorrowingRecords;
    }

    public List<BookWithBorrowingRecordDTO> getBooksWithBorrowingRecords2() {
        List<Map<String, Object>> results = bookRepository.findBooksWithBorrowingRecords2();
        List<BookWithBorrowingRecordDTO> bookWithBorrowingRecords = new ArrayList<>();

        for (Map<String, Object> result : results) {
            BookDTO bookDTO = mapToBookDTO(result);
            BorrowingRecordDTO borrowingRecordDTO = mapToBorrowingRecordDTO(result);

            BookWithBorrowingRecordDTO bookWithBorrowingRecordDTO = new BookWithBorrowingRecordDTO();
            bookWithBorrowingRecordDTO.setBook(bookDTO);
            bookWithBorrowingRecordDTO.setBorrowingRecord(borrowingRecordDTO);

            bookWithBorrowingRecords.add(bookWithBorrowingRecordDTO);
        }

        return bookWithBorrowingRecords;
    }

    private BookDTO mapToBookDTO(Map<String, Object> result) {
        BookDTO bookDTO = BookDTO.builder()
                .bookId((Integer) result.get("book_id"))
                .title((String) result.get("title"))
                .author((String) result.get("author"))
                .build();
        return bookDTO;
    }

    private BorrowingRecordDTO mapToBorrowingRecordDTO(Map<String, Object> result) {
        BorrowingRecordDTO borrowingRecordDTO = BorrowingRecordDTO.builder()
                .recordId((Integer) result.get("record_id"))
                .borrowerName((String) result.get("borrower_name"))
                .build();
        return borrowingRecordDTO;
    }
}
