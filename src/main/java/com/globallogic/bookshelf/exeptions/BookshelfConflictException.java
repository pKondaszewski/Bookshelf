
package com.globallogic.bookshelf.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BookshelfConflictException extends BookshelfException {

    public BookshelfConflictException(String message) {
        super(message);
    }
}
