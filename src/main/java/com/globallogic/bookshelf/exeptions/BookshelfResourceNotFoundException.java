package com.globallogic.bookshelf.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookshelfResourceNotFoundException extends BookshelfException {

    public BookshelfResourceNotFoundException(String message) {
        super(message);
    }

}
