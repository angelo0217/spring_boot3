package com.example.demo.controller;

import com.example.demo.entity.dto.BookDTO;
import com.example.demo.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/book")
public class Books {

    private final BookService bookService;

    public Books(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping("/insert")
    public BookDTO insertBook(@RequestBody BookDTO bookDTO){
        var dto = this.bookService.create(bookDTO);
        return dto;
    }
}
