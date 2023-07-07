package com.example.demo.entity.db;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "genre")
    private String genre;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private int quantity;
}


