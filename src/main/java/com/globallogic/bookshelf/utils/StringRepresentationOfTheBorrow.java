package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import lombok.NoArgsConstructor;

/**
 * Special class to create a user-friendly output of borrow
 */
@NoArgsConstructor
public class StringRepresentationOfTheBorrow {

    public String stringRepresentationOfUncompletedBorrow(Book book, Borrow borrow) {
        return "Author = " + book.getAuthor() + ", " +
                "Title = " + book.getTitle() + ", " +
                "Comment = " + borrow.getComment() + ", " +
                "Category name = " + book.getCategory().getName() + ", " +
                "Date of the borrow = " + borrow.getBorrowed();
    }

     public String stringRepresentationOfCompletedBorrow(Book book, Borrow borrow) {
         return stringRepresentationOfUncompletedBorrow(book, borrow) + ", " +
                "Date of the return = " + borrow.getReturned();
     }

}
