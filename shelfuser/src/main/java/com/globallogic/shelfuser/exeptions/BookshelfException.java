package com.globallogic.shelfuser.exeptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class BookshelfException extends RuntimeException {

    public BookshelfException(String message) {
        super(message);
        log.error(message);
    }

}
