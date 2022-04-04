package com.globallogic.bookshelf.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookshelfResourceNotFound extends BookshelfException {

    public BookshelfResourceNotFound(String message) {
        super(message);
    }
}