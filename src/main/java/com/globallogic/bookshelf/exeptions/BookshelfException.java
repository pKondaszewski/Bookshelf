package com.globallogic.bookshelf.exeptions;

import lombok.Getter;

@Getter
public class BookshelfException extends RuntimeException{

    public BookshelfException(String message) { super(message); }

    public BookshelfException(Throwable e, String message) { super(message); }
}