package com.globallogic.bookshelf.exeptions;

public class ReservationConflictException extends BookshelfConflictException {
    public ReservationConflictException(String message) {
        super(message);
    }
}
