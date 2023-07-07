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

@Service
public class BookService extends BaseService<BookDTO, Book, Integer> {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        super(bookRepository);
        this.bookRepository = bookRepository;
    }


    public List<BookWithBorrowingRecordDTO> getBooksWithBorrowingRecords() {
        List<Object[]> results = bookRepository.findBooksWithBorrowingRecords();
        List<BookWithBorrowingRecordDTO> bookWithBorrowingRecords = new ArrayList<>();

        for (Object[] result : results) {
            Book book = (Book) result[0];
            BorrowingRecord borrowingRecord = (BorrowingRecord) result[1];

            BookDTO bookDTO = mapBookToDTO(book);
            BorrowingRecordDTO borrowingRecordDTO = mapBorrowingRecordToDTO(borrowingRecord);

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

    public static BookDTO mapBookToDTO(Book book) {
        BookDTO bookDTO = BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationDate(book.getPublicationDate())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .price(book.getPrice())
                .quantity(book.getQuantity())
                .build();

        return bookDTO;
    }
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

    @Override
    protected Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setBookId(bookDTO.getBookId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setIsbn(bookDTO.getIsbn());
        book.setGenre(bookDTO.getGenre());
        book.setPrice(bookDTO.getPrice());
        book.setQuantity(bookDTO.getQuantity());
        return book;
    }

    @Override
    protected BookDTO convertToDto(Book book) {
        var bookDTO = BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationDate(book.getPublicationDate())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .price(book.getPrice())
                .quantity(book.getQuantity())
                .build();
        return bookDTO;
    }

    @Override
    protected Integer getDtoId(BookDTO bookDTO) {
        return bookDTO.getBookId();
    }
}
