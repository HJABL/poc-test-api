package com.europcar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books/discount/{percentage}")
    public List<Book> discountAllBook(@PathVariable Integer percentage) {
        Book[] book = bookService.getBook();
        return bookService.changePrice(book, percentage);
    }
}
