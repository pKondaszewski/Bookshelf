package com.globallogic.bookshelf.exeptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BookshelfException extends RuntimeException {

    public BookshelfException(String message) {
        super(message);
        log.error(message);
    }

}