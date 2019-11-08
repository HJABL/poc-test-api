package com.europcar.error;

import java.util.Set;

public class BookApiUnavailableException extends RuntimeException {

    public BookApiUnavailableException() {
        super("!!! Service Unavailable !!!");
    }

}
