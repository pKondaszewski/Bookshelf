package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Borrow;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserHistory {
    private List<Borrow> returnedBooks;
    private String currentlyBorrowedBooksTitles;
    private int numberOfCurrentlyBorrowedBooks;
}
