package com.example.demo.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.entity.db.Book;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BookDTO {
    private int bookId;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private String isbn;
    private String genre;
    private BigDecimal price;
    private int quantity;

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
}