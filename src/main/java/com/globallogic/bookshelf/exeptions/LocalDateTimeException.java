package com.globallogic.bookshelf.exeptions;

public class LocalDateTimeException extends BookshelfConflictException {
    public LocalDateTimeException(String message) {
        super(message);
    }
}
