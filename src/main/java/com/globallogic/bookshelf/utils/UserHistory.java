package com.globallogic.bookshelf.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserHistory {
    private List<String> returnedBooks;
    private List<String> currentlyBorrowedBooks;
    private int numberOfCurrentlyBorrowedBooks;
}
