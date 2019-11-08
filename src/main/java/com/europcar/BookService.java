package com.europcar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookService {

    private static final int ONE_HUNDRED = 100;
    
    @Autowired
    private BookClient bookClient;

    Book[] getBook() {
        return bookClient.getBooks();
    }

    List<Book> changePrice(Book[] books, Integer percentage) {
        List<Book> listBooks = List.of(books);
        listBooks.forEach(x -> x.setPrice(applyPercentage(x, percentage)));

        return listBooks;
    }

    private BigDecimal applyPercentage(Book book, Integer percentage) {
        BigDecimal price = book.getPrice();
        return price.subtract(price.multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(ONE_HUNDRED)));
    }
}
